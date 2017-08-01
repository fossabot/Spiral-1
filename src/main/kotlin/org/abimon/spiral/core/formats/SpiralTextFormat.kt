package org.abimon.spiral.core.formats

import org.abimon.spiral.core.SpiralDrill
import org.abimon.spiral.core.drills.DrillHead
import org.abimon.spiral.core.objects.CustomLin
import org.abimon.visi.io.DataSource
import org.abimon.visi.lang.make
import java.io.OutputStream

object SpiralTextFormat : SpiralFormat {
    override val name = "SPIRAL Text"
    override val extension = ".stxt"
    override val conversions: Array<SpiralFormat> = arrayOf(LINFormat)

    override fun isFormat(source: DataSource): Boolean = !SpiralDrill.stxtRunner.run(String(source.data, Charsets.UTF_8)).hasErrors()

    override fun convert(format: SpiralFormat, source: DataSource, output: OutputStream, params: Map<String, Any?>) {
        super.convert(format, source, output, params)

        when (format) {
            is LINFormat -> {
                val lin = make<CustomLin> {
                    SpiralDrill.stxtRunner.run(String(source.data, Charsets.UTF_8)).valueStack.forEach { value ->
                        if (value is List<*>) (value[0] as DrillHead).formScripts(value.subList(1, value.size).filterNotNull().toTypedArray()).forEach { scriptEntry -> entry(scriptEntry) }
                    }
                }

                lin.compile(output)
            }
        }
    }
}