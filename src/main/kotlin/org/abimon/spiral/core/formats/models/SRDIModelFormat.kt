package org.abimon.spiral.core.formats.models

import org.abimon.spiral.core.formats.SpiralFormat
import org.abimon.spiral.core.objects.game.DRGame
import org.abimon.spiral.core.objects.models.SRDIMesh
import org.abimon.spiral.core.objects.models.SRDIModel
import org.abimon.spiral.mvc.gurren.Gurren
import org.abimon.spiral.util.InputStreamFuncDataSource
import java.io.InputStream
import java.io.OutputStream
import java.io.PrintStream

object SRDIModelFormat: SpiralFormat {
    override val name: String = "SRDI"
    override val extension: String? = "srdi"
    override val conversions: Array<SpiralFormat> = arrayOf(OBJModelFormat)

    override fun isFormat(game: DRGame?, name: String?, dataSource: () -> InputStream): Boolean {
        try {
            return SRDIModel(InputStreamFuncDataSource(dataSource)).meshes.any { mesh -> mesh.isValidMesh }
        } catch(illegal: IllegalArgumentException) {

        }

        return false
    }

    override fun convert(game: DRGame?, format: SpiralFormat, name: String?, dataSource: () -> InputStream, output: OutputStream, params: Map<String, Any?>): Boolean {
        if(super.convert(game, format, name, dataSource, output, params)) return true

        val flipUVs = "${params["srdi:flipUVs"] ?: true}".toBoolean()

        val srdi = SRDIModel(InputStreamFuncDataSource(dataSource))
        when(format) {
            OBJModelFormat -> {
                val out = PrintStream(output)


                out.println("# SPIRAL v${Gurren.version}")
                out.println("# Autogenerated")
                out.println()

                srdi.meshes.forEach { mesh ->
                    if(mesh.isValidMesh) {
                        mesh.vertices.forEach { (x, y, z) -> out.println("v $x $y $z") }

                        if(flipUVs)
                            mesh.uvs.forEach { (u, v) -> out.println("vt ${1.0 - u} ${1.0 - v}") }
                        else
                            mesh.uvs.forEach { (u, v) -> out.println("vt $u $v") }

                        if(mesh.vertices.size == mesh.uvs.size)
                            mesh.faces.forEach { (a, b, c) -> out.println("f ${a + 1}/${a + 1} ${b + 1}/${b + 1} ${c + 1}/${c + 1}") }
                        else
                            mesh.faces.forEach { (a, b, c) -> out.println("f ${a + 1} ${b + 1} ${c + 1}") }
                    }
                }
            }
        }

        return true
    }

    val SRDIMesh.isValidMesh: Boolean
        get() = (this.vertices.isNotEmpty() && this.faces.isNotEmpty()) || (this.uvs.isNotEmpty() && this.faces.isNotEmpty())
}