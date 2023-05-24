#![cfg_attr(
all(not(debug_assertions), target_os = "windows"),
windows_subsystem = "windows"
)]

use std::borrow::BorrowMut;
use std::fmt::Display;
use std::fs;
use std::path::PathBuf;

use dialog::DialogBox;
use tauri::{AppHandle, Manager, RunEvent, Runtime, State};
use tauri::api::process::{Command, CommandChild, CommandEvent};

fn main() {
    let child_process = new_child_process();
    let child_process_state = ChildProcessState {
        mutex: std::sync::Mutex::new(child_process),
        console: std::sync::Mutex::new(Vec::new()),
    };

    tauri::Builder::default()
        .manage(child_process_state)
        .setup(move |app| {
            let state: State<ChildProcessState> = app.state();
            send_console(&app.handle(), "Welcome to Linkie!");
            send_console(&app.handle(), "Linkie: Tauri is still in development, so expect bugs and crashes.");
            send_console(&app.handle(), "Environment Directory: ".to_owned() + &std::env::current_dir().unwrap().to_string_lossy().to_string());
            send_console(&app.handle(), "Cache Directory: ".to_owned() + &app.path_resolver().app_cache_dir().unwrap().to_string_lossy().to_string());
            send_console(&app.handle(), "Resources Directory: ".to_owned() + &app.path_resolver().resource_dir().unwrap().to_string_lossy().to_string());
            send_console(&app.handle(), "Logs Directory: ".to_owned() + &app.path_resolver().app_log_dir().unwrap().to_string_lossy().to_string());
            send_console(&app.handle(), "Starting child process...");
            start_backend(&app.handle(), state
                .mutex
                .lock()
                .unwrap()
                .borrow_mut(),
            )
                .expected(&app.handle(), "Failed to start child process");
            Ok(())
        })
        .invoke_handler(tauri::generate_handler![
            start_server,
            stop_server,
            restart_server
        ])
        .build(tauri::generate_context!())
        .expect("error while running tauri application")
        .run(|apphandle, event| match event {
            RunEvent::Exit => {
                let state: State<ChildProcessState> = apphandle.state();
                terminate_backend(apphandle, state
                    .mutex
                    .lock()
                    .unwrap()
                    .borrow_mut(),
                ).expected(&apphandle, "Failed to terminate child process");
            }
            _ => {}
        })
}

fn send_console<R: Runtime>(app: &AppHandle<R>, message: impl Into<String>) {
    let state: State<ChildProcessState> = app.state();
    let msg = message.into();
    state.console.lock().unwrap().push(msg.clone());
    println!("{}", msg);

    while state.console.lock().unwrap().len() > 1000 {
        state.console.lock().unwrap().remove(0);
    }

    #[derive(serde::Serialize, serde::Deserialize, Clone)]
    struct Payload {
        messages: Vec<String>,
    }

    app.emit_all("console", Payload {
        messages: state.console.lock().unwrap().clone(),
    }).expect("Failed to send console message");
}

fn show_error(message: impl Into<String>) {
    dialog::Message::new(message)
        .title("Error Occurred")
        .show()
        .expect("Could not display dialog box");
}

pub struct ChildProcess {
    child: Option<CommandChild>,
}

struct ChildProcessState {
    mutex: std::sync::Mutex<ChildProcess>,
    console: std::sync::Mutex<Vec<String>>,
}

trait ExpectedErrorResult<T> {
    fn expected<R: Runtime>(self, app: &AppHandle<R>, message: impl Into<String>) -> T;
}

impl<T, E : std::fmt::Debug> ExpectedErrorResult<T> for Result<T, E> {
    fn expected<R: Runtime>(self, app: &AppHandle<R>, message: impl Into<String>) -> T {
        match self {
            Ok(t) => t,
            Err(e) => {
                let msg = message.into();
                let message = format!("{msg}: {e:?}");
                send_console(app, message.clone());
                show_error(message.clone());
                panic!("{}", message);
            },
        }
    }
}

impl<T> ExpectedErrorResult<T> for Option<T> {
    fn expected<R: Runtime>(self, app: &AppHandle<R>, message: impl Into<String>) -> T {
        match self {
            Some(t) => t,
            None => {
                let message = message.into();
                send_console(app, message.clone());
                show_error(message.clone());
                panic!("{}", message);
            },
        }
    }
}

#[tauri::command]
fn start_server<R: Runtime>(app: AppHandle<R>, state: State<ChildProcessState>) -> Result<String, String> {
    return start_backend(&app, state
        .mutex
        .lock()
        .unwrap()
        .borrow_mut());
}

#[tauri::command]
fn stop_server<R: Runtime>(app: AppHandle<R>, state: State<ChildProcessState>) -> Result<String, String> {
    return terminate_backend(&app, state
        .mutex
        .lock()
        .unwrap()
        .borrow_mut());
}

#[tauri::command]
fn restart_server<R: Runtime>(app: AppHandle<R>, state: State<ChildProcessState>) -> Result<String, String> {
    return restart_backend(&app, state
        .mutex
        .lock()
        .unwrap()
        .borrow_mut());
}

pub fn new_child_process() -> ChildProcess {
    ChildProcess {
        child: None,
    }
}

pub fn start_backend<R: Runtime>(app: &AppHandle<R>, process: &mut ChildProcess) -> Result<String, String> {
    match process.child.borrow_mut() {
        Some(_) => {
            let info = "Child process already existed, skipping";
            send_console(app, info);
            Ok(info.into())
        }
        None => {
            let java_exc_name = if cfg!(windows) {
                "java.exe"
            } else {
                "java"
            };
            let mut java_path: PathBuf = app.path_resolver().resolve_resource("../../backend/build/libs/jdk-17-minjre/bin/".to_owned() + java_exc_name.clone())
                .expected(&app, "Could not find jre");
            java_path = fs::canonicalize(java_path).unwrap();
            let backend_jar: PathBuf = app.path_resolver().resolve_resource("../../backend/build/libs/linkie-web-backend.jar")
                .expected(&app, "Could not find backend jar");
            send_console(&app, "Java at ".to_owned() + java_path.to_str().unwrap());
            send_console(&app, "Backend jar at ".to_owned() + backend_jar.to_str().unwrap());
            if !java_path.exists() {
                return Err(("Could not find java executable ".to_owned() + java_path.to_str().unwrap()).into());
            } else if !backend_jar.exists() {
                return Err(("Could not find backend jar ".to_owned() + backend_jar.to_str().unwrap()).into());
            }
            let working_dir: PathBuf = app.path_resolver().app_cache_dir().unwrap();
            send_console(&app, "Working dir at ".to_owned() + working_dir.to_str().unwrap());
            fs::create_dir_all(working_dir.clone()).expected(&app, "Failed to create working dir");
            fs::copy(backend_jar, working_dir.join("linkie-web-backend.jar"))
                .expected(&app, "Failed to copy backend jar");
            // java_path can start with \\?\, which is not supported by Command::new
            let java_path_str = java_path.to_str().unwrap().replace("\\\\?\\", "");
            let tt = Command::new(java_path_str)
                .args(["-jar", "linkie-web-backend.jar"])
                .current_dir(working_dir);
            let pair = tt.spawn();
            match pair {
                Ok((mut rx, v)) => {
                    let handle = app.app_handle();
                    tauri::async_runtime::spawn(async move {
                        while let Some(event) = rx.recv().await {
                            if let CommandEvent::Stdout(out) = event {
                                send_console(&handle, &out);
                            } else if let CommandEvent::Stderr(line) = event {
                                send_console(&handle, &line);
                            } else if let CommandEvent::Error(line) = event {
                                send_console(&handle, &line);
                                show_error(&line);
                            } else if let CommandEvent::Terminated(payload) = event {
                                let code = payload.code.or(payload.signal).map(|i| i.to_string()).unwrap_or("unknown".to_owned());
                                let msg = format!("Child process exited with code {}", code);
                                send_console(&handle, &msg);
                                show_error(&msg);
                            }
                        }
                    });
                    process.child = Some(v);
                    let info = "Successfully started child process";
                    send_console(app, info);
                    Ok(info.into())
                }
                Err(e) => {
                    let info = "Failed to start child process: ".to_owned() + &e.to_string();
                    send_console(app, &info);
                    Err(info.into())
                }
            }
        }
    }
}

pub fn terminate_backend<R: Runtime>(app: &AppHandle<R>, process: &mut ChildProcess) -> Result<String, String> {
    if process.child.is_some() {
        let child = std::mem::replace(&mut process.child, None);
        child.expected(&app, "Failed to find child process")
            .kill()
            .expected(&app, "Failed to kill child process");
        process.child = None;
        let info = "Child process terminated";
        send_console(app, info);
        Ok(info.into())
    } else {
        let info = "Child process does not exist, skipping termination";
        send_console(app, info);
        Ok(info.into())
    }
}

pub fn restart_backend<R: Runtime>(app: &AppHandle<R>, process: &mut ChildProcess) -> Result<String, String> {
    let terminate_result = terminate_backend(app, process);
    match terminate_result {
        Ok(_) => {
            send_console(app, "Successfully terminated child process");
            match start_backend(app, process) {
                Ok(_) => {
                    let info = "Successfully restarted child process";
                    send_console(app, info);
                    Ok(info.into())
                }
                Err(e) => {
                    send_console(app, &e);
                    return Err(e.into());
                }
            }
        }
        Err(e) => {
            send_console(app, &e);
            return Err(e);
        }
    }
}
