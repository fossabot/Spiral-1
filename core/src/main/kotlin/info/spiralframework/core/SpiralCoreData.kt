package info.spiralframework.core

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import info.spiralframework.core.serialisation.InstantSerialisation
import java.io.File
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption
import java.security.MessageDigest
import java.text.MessageFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * This singleton holds important information for all Spiral modules
 * @author UnderMybrella
 */
object SpiralCoreData {
    /** Jackson mapper for JSON data */
    val JSON_MAPPER: ObjectMapper = ObjectMapper()
            .registerKotlinModule()
            .registerModules(Jdk8Module(), JavaTimeModule(), ParameterNamesModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)

    /** Jackson mapper for YAML data */
    val YAML_MAPPER: ObjectMapper = ObjectMapper(YAMLFactory())
            .registerKotlinModule()
            .registerModules(Jdk8Module(), JavaTimeModule(), ParameterNamesModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)

    /** Jackson mapper for XML data */
    val XML_MAPPER: ObjectMapper = XmlMapper(JacksonXmlModule().apply { setDefaultUseWrapper(false) })
            .registerKotlinModule()
            .registerModules(Jdk8Module(), JavaTimeModule(), ParameterNamesModule(), InstantSerialisation.MODULE())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET)
            .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
            .setDefaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY))

    /** Steam ID for Danganronpa: Trigger Happy Havoc */
    val STEAM_DANGANRONPA_TRIGGER_HAPPY_HAVOC = "413410"
    /** Steam ID for Danganronpa 2: Goodbye Despair */
    val STEAM_DANGANRONPA_2_GOODBYE_DESPAIR = "413420"

    /** 'File Name' for Spiral header data, to be used in archives */
    val SPIRAL_HEADER_NAME = "Spiral-Header"
    /**
     * 'File Name' for Spiral mod list data, to be used in archives
     * This file should ideally keep track of mods currently installed, and their files + versions
     * */
    val SPIRAL_MOD_LIST = "Spiral-Mod-List"

    /**
     * An MD5 hash of the running JAR file, or null if we're not running from a JAR file (developer directory)
     */
    val version: String? by lazy {
        val file = File(SpiralCoreData::class.java.protectionDomain.codeSource.location.path)
        if (!file.isFile)
            return@lazy null

        val md = MessageDigest.getInstance("MD5")

        val channel = FileChannel.open(file.toPath(), StandardOpenOption.READ)
        val buffer = ByteBuffer.allocate(8192)

        while (channel.isOpen) {
            val read = channel.read(buffer)
            if (read <= 0)
                break


            buffer.flip()
            md.update(buffer)
            buffer.rewind()
        }

        return@lazy String.format("%032x", BigInteger(1, md.digest()))
    }

    val _localisationBundles: MutableList<ResourceBundle> = ArrayList()
    val localisationBundles: List<ResourceBundle> = _localisationBundles

    val _englishBundles: MutableList<ResourceBundle> = ArrayList()
    val englishBundles: List<ResourceBundle> = _englishBundles

    fun localise(base: String, vararg values: Any): String {
        val msg = localisationBundles.first { bundle -> bundle.containsKey(base) }.getString(base)
        return MessageFormat.format(msg, *values)
    }

    fun localiseForEnglish(base: String, vararg values: Any): String {
        val msg = englishBundles.first { bundle -> bundle.containsKey(base) }.getString(base)
        return MessageFormat.format(msg, *values)
    }

    fun changeLanguage(locale: Locale) {
        val oldArray = localisationBundles.toTypedArray()
        _localisationBundles.clear()
        _localisationBundles.addAll(oldArray.map { bundle -> ResourceBundle.getBundle(bundle.baseBundleName, locale) })
    }

    fun addBundle(bundleName: String) {
        _localisationBundles.add(ResourceBundle.getBundle(bundleName))
        _englishBundles.add(ResourceBundle.getBundle(bundleName, Locale.ENGLISH))
    }
}