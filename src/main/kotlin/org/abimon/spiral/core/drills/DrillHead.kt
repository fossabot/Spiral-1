package org.abimon.spiral.core.drills

import org.abimon.spiral.core.lin.LinScript
import org.parboiled.BaseParser
import org.parboiled.Rule

interface DrillHead {
    fun Syntax(parser: BaseParser<Any>): Rule
    fun formScripts(rawParams: Array<Any>): Array<LinScript>
}