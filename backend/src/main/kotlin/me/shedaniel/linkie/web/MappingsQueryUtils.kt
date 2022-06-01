package me.shedaniel.linkie.web

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.shedaniel.linkie.MappingsContainer
import me.shedaniel.linkie.MappingsEntryType
import me.shedaniel.linkie.utils.ClassResultList
import me.shedaniel.linkie.utils.FieldResultList
import me.shedaniel.linkie.utils.MappingsQuery
import me.shedaniel.linkie.utils.MatchAccuracy
import me.shedaniel.linkie.utils.MethodResultList
import me.shedaniel.linkie.utils.QueryContext
import me.shedaniel.linkie.utils.ResultHolder

object MappingsQueryUtils {
    data class Result(
        val results: MutableList<ResultHolder<*>>,
        val fuzzy: Boolean,
    )

    suspend fun query(mappings: MappingsContainer, searchTerm: String, vararg types: MappingsEntryType): Result {
        require(types.isNotEmpty())
        val context = QueryContext(
            provider = { mappings },
            searchKey = searchTerm,
        )
        val result: MutableList<ResultHolder<*>> = mutableListOf()
        var classes: ClassResultList? = null
        var methods: MethodResultList? = null
        var fields: FieldResultList? = null
        runBlocking {
            if (MappingsEntryType.CLASS in types) {
                launch {
                    try {
                        classes = MappingsQuery.queryClasses(context).value
                    } catch (ignore: NullPointerException) {
                    }
                }
            }
            if (MappingsEntryType.METHOD in types) {
                launch {
                    try {
                        methods = MappingsQuery.queryMethods(context).value
                    } catch (ignore: NullPointerException) {
                    }
                }
            }
            if (MappingsEntryType.FIELD in types) {
                launch {
                    try {
                        fields = MappingsQuery.queryFields(context).value
                    } catch (ignore: NullPointerException) {
                    }
                }
            }
        }
        classes?.also(result::addAll)
        methods?.also(result::addAll)
        fields?.also(result::addAll)
        result.sortByDescending { it.score }

        if (result.isNotEmpty()) {
            return Result(result, false)
        }

        runBlocking {
            if (MappingsEntryType.CLASS in types) {
                launch {
                    try {
                        classes = MappingsQuery.queryClasses(context.copy(accuracy = MatchAccuracy.Fuzzy)).value
                    } catch (ignored: NullPointerException) {
                    }
                }
            }
            if (MappingsEntryType.METHOD in types) {
                launch {
                    try {
                        methods = MappingsQuery.queryMethods(context.copy(accuracy = MatchAccuracy.Fuzzy)).value
                    } catch (ignored: NullPointerException) {
                    }
                }
            }
            if (MappingsEntryType.FIELD in types) {
                launch {
                    try {
                        fields = MappingsQuery.queryFields(context.copy(accuracy = MatchAccuracy.Fuzzy)).value
                    } catch (ignored: NullPointerException) {
                    }
                }
            }
        }
        classes?.also(result::addAll)
        methods?.also(result::addAll)
        fields?.also(result::addAll)
        result.sortByDescending { it.score }

        if (result.isEmpty()) {
            if (types.size != 1) {
                MappingsQuery.errorNoResultsFound(null, searchTerm)
            } else {
                MappingsQuery.errorNoResultsFound(types.first(), searchTerm)
            }
        }

        return Result(result, true)
    }
}