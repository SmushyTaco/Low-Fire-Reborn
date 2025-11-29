package com.smushytaco.low_fire_reborn
import io.wispforest.owo.config.annotation.Config
import io.wispforest.owo.config.annotation.Modmenu
@Modmenu(modId = LowFire.MOD_ID)
@Config(name = LowFire.MOD_ID, wrapperName = "ModConfig")
@Suppress("UNUSED")
class ModConfiguration {
    @JvmField
    var enableLowFire = true
    @JvmField
    var shouldRenderFire = true
    @JvmField
    var fireOffset = -0.3
    @JvmField
    var fireOffsetCycleUpperKeyLimit = 0.0
    @JvmField
    var fireOffsetCycleLowerKeyLimit = -0.5
    @JvmField
    var fireOffsetChange = 0.1
}