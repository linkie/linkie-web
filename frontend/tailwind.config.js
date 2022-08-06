module.exports = {
    content: [
        "./index.html",
        "./src/**/*.{vue,js,ts,jsx,tsx}",
    ],
    theme: {
        extend: {},
    },
    plugins: [
        require("daisyui"),
        require("tailwind-gradient-mask-image"),
    ],
    daisyui: {
        themes: [
            {
                cupcake: {
                    ...require("daisyui/src/colors/themes")["[data-theme=cupcake]"],
                    primary: "#ef9fbc",
                    secondary: "#65c3c8",
                    neutral: "#471f2c",
                    "base-content": "#471f2c",
                },
            },
            "dark",
        ],
    },
}
