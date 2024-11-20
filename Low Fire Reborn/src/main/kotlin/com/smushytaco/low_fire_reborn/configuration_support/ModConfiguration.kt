package com.smushytaco.low_fire_reborn.configuration_support
import com.smushytaco.low_fire_reborn.LowFire
import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
@Config(name = LowFire.MOD_ID)
class ModConfiguration: ConfigData {
    var enableLowFire = true
    var shouldRenderFire = true
    var fireOffset = -0.3
    var fireOffsetCycleUpperKeyLimit = 0.0
    var fireOffsetCycleLowerKeyLimit = -0.5
    val fireOffsetChange = 0.1
}