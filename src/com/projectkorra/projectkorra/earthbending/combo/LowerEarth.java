package com.projectkorra.projectkorra.earthbending.combo;

import java.util.ArrayList;

import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.EarthAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager.AbilityInformation;
import com.projectkorra.projectkorra.earthbending.CollapseWall;
import com.projectkorra.projectkorra.util.ClickType;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LowerEarth extends EarthAbility implements ComboAbility {

    public LowerEarth(Player player) {
        super(player);
        if (!this.bPlayer.canBendIgnoreBinds(this)) {
			return;
		}
        CollapseWall collapseWall = new CollapseWall(player, true);
        collapseWall.setCooldown(collapseWall.getCooldown());//this line might be unnessesary? not sure.
        bPlayer.addCooldown(this, collapseWall.getCooldown());
    }

    @Override
    public void progress() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isSneakAbility() {
        return false;
    }

    @Override
    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public long getCooldown() {
        return getConfig().getLong("Abilities.Earth.Collapse.Cooldown");
    }

    @Override
    public String getName() {
        return "LowerEarth";
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public Object createNewComboInstance(Player player) {
        return new LowerEarth(player);
    }

    @Override
    public ArrayList<AbilityInformation> getCombination() {
        final ArrayList<AbilityInformation> lowerEarth = new ArrayList<>();
		lowerEarth.add(new AbilityInformation("RaiseEarth", ClickType.RIGHT_CLICK_BLOCK));
		lowerEarth.add(new AbilityInformation("RaiseEarth", ClickType.RIGHT_CLICK_BLOCK));
		return lowerEarth;
	}



}