package org.abimon.osl.drills

import org.abimon.osl.AllButMatcher
import org.abimon.osl.OpenSpiralLanguageParser
import org.abimon.spiral.core.objects.scripting.lin.LinScript
import org.abimon.spiral.core.objects.scripting.lin.SpriteEntry
import org.parboiled.Action
import org.parboiled.Rule
import kotlin.reflect.KClass

object BustSpriteDrill : DrillHead<LinScript> {
    val cmd: String = "BUST-SPRITE"
    val NAME = AllButMatcher(charArrayOf(':', '\n'))
    val NUMERAL_REGEX = "\\d+".toRegex()

    override val klass: KClass<LinScript> = LinScript::class

    override fun OpenSpiralLanguageParser.syntax(): Rule =
            FirstOf(
                    Sequence(
                            clearTmpStack(cmd),
                            "Display sprite for ",
                            pushTmpAction(cmd, this@BustSpriteDrill),
                            FirstOf(
                                    Parameter(cmd),
                                    Sequence(
                                            OneOrMore(Digit()),
                                            pushTmpAction(cmd)
                                    )
                            ),
                            Action<Any> {
                                val name = peekTmpAction(cmd)?.toString() ?: ""
                                return@Action name in customIdentifiers || name in game.characterIdentifiers || name.matches(NUMERAL_REGEX)
                            },
                            " with ID ",
                            OneOrMore(Digit()),
                            pushTmpAction(cmd),
                            pushTmpStack(cmd)
                    ),
                    Sequence(
                            "a",
                            "s"
                    )
            )

    override fun operate(parser: OpenSpiralLanguageParser, rawParams: Array<Any>): LinScript {
        val characterStr = rawParams[0].toString()
        val character = parser.customIdentifiers[characterStr] ?: parser.game.characterIdentifiers[characterStr] ?: characterStr.toIntOrNull() ?: 0
        val sprite = rawParams[1].toString().toIntOrNull() ?: 0

        return SpriteEntry(0, character, sprite, 1, 0)
    }
}