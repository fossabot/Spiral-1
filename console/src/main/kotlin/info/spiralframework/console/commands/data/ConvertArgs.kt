package info.spiralframework.console.commands.data

import java.io.File

class ConvertArgs {
    data class Immutable(val converting: File?, var from: String?, var to: String?, var filter: Regex?)

    var converting: File? = null
    var from: String? = null
    var to: String? = null
    var filter: Regex? = null
    var builder: Boolean = false

    fun makeImmutable(
            defaultConverting: File? = null,
            defaultFrom: String? = null,
            defaultTo: String? = null,
            defaultFilter: Regex? = null
    ): ConvertArgs.Immutable =
            Immutable(
                    converting ?: defaultConverting,
                    from ?: defaultFrom,
                    to ?: defaultTo,
                    filter ?: defaultFilter
            )
}