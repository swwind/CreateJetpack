package com.possible_triangle.create_jetpack.item

import com.possible_triangle.create_jetpack.Content.JETPACK_CAPABILITY
import com.possible_triangle.create_jetpack.capability.IJetpack
import com.possible_triangle.create_jetpack.capability.IJetpack.Context
import com.possible_triangle.create_jetpack.config.Configs
import com.simibubi.create.content.curiosities.armor.BackTankUtil
import com.simibubi.create.content.curiosities.armor.CopperBacktankItem
import com.tterrag.registrate.util.entry.ItemEntry
import net.minecraft.core.Direction
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.Rarity
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional

class BronzeJetpack(properties: Properties, blockItem: ItemEntry<CopperBacktankBlockItem>) :
    CopperBacktankItem(properties.rarity(Rarity.RARE), blockItem), IJetpack, ICapabilityProvider {
    private val capability = LazyOptional.of<IJetpack> { this }

    override fun hoverSpeed(context: Context): Double {
        return Configs.SERVER.hoverSpeed
    }

    override fun verticalSpeed(context: Context): Double {
        return Configs.SERVER.verticalSpeed
    }

    override fun activeType(context: Context): ControlType {
        return ControlType.TOGGLE
    }

    override fun hoverType(context: Context): ControlType {
        return ControlType.TOGGLE
    }

    override fun horizontalSpeed(context: Context): Double {
        return Configs.SERVER.horizontalSpeed
    }

    override fun acceleration(context: Context): Double {
        return Configs.SERVER.acceleration
    }

    override fun swimModifier(context: Context): Double {
        return Configs.SERVER.swimModifier
    }

    private val thrusters = listOf(-0.35, 0.35).map { offset ->
        Vec3(offset, 0.7, -0.5)
    }

    override fun getThrusters(context: Context) = thrusters

    override fun onUse(context: Context) {
        if (!isThrusting(context)) return
        BackTankUtil.canAbsorbDamage(context.entity, usesPerTank(context))
    }

    private fun usesPerTank(context: Context): Int {
        return if (isHovering(context)) Configs.SERVER.usesPerTankHover
        else Configs.SERVER.usesPerTank
    }

    override fun isValid(context: Context): Boolean {
        return context.slot == EquipmentSlot.CHEST
    }

    override fun isUsable(context: Context): Boolean {
        val tank = BackTankUtil.get(context.entity)
        if (tank.isEmpty) return false
        val air = BackTankUtil.getAir(tank)
        val cost = BackTankUtil.maxAirWithoutEnchants() / usesPerTank(context)
        return air >= cost
    }

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        if (cap == JETPACK_CAPABILITY) return capability.cast()
        return LazyOptional.empty()
    }

    enum class ControlType {
        ALWAYS, NEVER, TOGGLE
    }

}