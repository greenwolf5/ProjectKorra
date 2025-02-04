package com.projectkorra.projectkorra.earthbending;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.ability.EarthAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.ClickType;

public class RaiseEarthWall extends EarthAbility {

	@Attribute(Attribute.SELECT_RANGE)
	private int selectRange;
	@Attribute(Attribute.HEIGHT)
	private int height;
	@Attribute(Attribute.WIDTH)
	private int width;
	@Attribute(Attribute.COOLDOWN)
	private long cooldown;
	private Location location;
	private Vector playerDirection;
	private Vector moveSourceThisDirection;

	public RaiseEarthWall(final Player player) {
		super(player);
		this.selectRange = getConfig().getInt("Abilities.Earth.RaiseEarth.Wall.SelectRange");
		this.height = getConfig().getInt("Abilities.Earth.RaiseEarth.Wall.Height");
		this.width = getConfig().getInt("Abilities.Earth.RaiseEarth.Wall.Width");
		this.cooldown = getConfig().getLong("Abilities.Earth.RaiseEarth.Wall.Cooldown");

		if (!this.bPlayer.canBend(this) || this.bPlayer.isOnCooldown("RaiseEarthWall")) {
			return;
		}

		if (this.bPlayer.isAvatarState()) {
			this.height = getConfig().getInt("Abilities.Avatar.AvatarState.Earth.RaiseEarth.Wall.Height");
			this.width = getConfig().getInt("Abilities.Avatar.AvatarState.Earth.RaiseEarth.Wall.Width");
		}
		playerDirection = player.getEyeLocation().getDirection();
		this.start();
	}

	private static Vector getDegreeRoundedVector(Vector vec, final double degreeIncrement) {
		if (vec == null) {
			return null;
		}
		vec = vec.normalize();
		final double[] dims = { vec.getX(), vec.getY(), vec.getZ() };

		for (int i = 0; i < dims.length; i++) {
			final double dim = dims[i];
			final int sign = dim >= 0 ? 1 : -1;
			final int dimDivIncr = (int) (dim / degreeIncrement);

			final double lowerBound = dimDivIncr * degreeIncrement;
			final double upperBound = (dimDivIncr + (1 * sign)) * degreeIncrement;

			if (Math.abs(dim - lowerBound) < Math.abs(dim - upperBound)) {
				dims[i] = lowerBound;
			} else {
				dims[i] = upperBound;
			}
		}
		return new Vector(dims[0], dims[1], dims[2]);
	}

	@Override
	public String getName() {
		return "RaiseEarth";
	}

	@Override
	public void progress() {
		final Vector direction = this.player.getEyeLocation().getDirection().normalize();
		double ox, oy, oz;
		direction.setY(0);
		ox = -direction.getZ();
		oy = 0;
		oz = direction.getX();

		Vector orth = new Vector(ox, oy, oz);
		orth = orth.normalize();
		orth = getDegreeRoundedVector(orth, 0.25);

		final Block sblock = BlockSource.getEarthSourceBlock(this.player, this.selectRange, ClickType.SHIFT_DOWN);

		if (sblock == null) {
			this.location = this.getTargetEarthBlock(this.selectRange).getLocation();
		} else {
			this.location = sblock.getLocation();
		}

		final World world = this.location.getWorld();
		boolean shouldAddCooldown = false;

		if(sblock != null){ //If you go out of range, sometimes it'll sort of work but error- so this helps prevent that
							//This also causes an issue when using at far range? Don't quiet understand, but if using above sourcerange, then tapping shift, sometimes it'll
							//detect a block that's off the x or z axis oh well. we'll see if it's a big issue.
		for(int p = 0; p < 2; p++){
		for (int i = 0; i < this.width; i++) {
			final double adjustedI = i - this.width / 2.0;
			Block block = world.getBlockAt(this.location.clone().add(orth.clone().multiply(adjustedI)));

			if (this.isTransparent(block)) {
				for (int j = 1; j < this.height; j++) {
					block = block.getRelative(BlockFace.DOWN);
					if (this.isEarthbendable(block)) {
						shouldAddCooldown = true;
						new RaiseEarth(this.player, block.getLocation(), this.height);
					} else if (!this.isTransparent(block)) {
						break;
					}
				}
			} else if (this.isEarthbendable(block.getRelative(BlockFace.UP))) {
				for (int j = 1; j < this.height; j++) {
					block = block.getRelative(BlockFace.UP);
					if (this.isTransparent(block)) {
						shouldAddCooldown = true;
						new RaiseEarth(this.player, block.getRelative(BlockFace.DOWN).getLocation(), this.height);
					} else if (!this.isEarthbendable(block)) {
						break;
					}
				}
			} else if (this.isEarthbendable(block)) {
				shouldAddCooldown = true;
				new RaiseEarth(this.player, block.getLocation(), this.height);
			}
		}

		moveSourceThisDirection = new Vector(playerDirection.getX(),0,playerDirection.getZ());
		moveSourceThisDirection = getDegreeRoundedVector(moveSourceThisDirection, 0.25);

		if(Math.abs(moveSourceThisDirection.getX()) > Math.abs(moveSourceThisDirection.getZ())){
			moveSourceThisDirection.setZ(0);
		}
		else{
			moveSourceThisDirection.setX(0);
		}

		location = (sblock.getLocation().add(moveSourceThisDirection));
	}
}

		if (shouldAddCooldown) {
			this.bPlayer.addCooldown("RaiseEarthWall", this.cooldown);
		}
		this.remove();
	}

	@Override
	public Location getLocation() {
		return this.location;
	}

	@Override
	public long getCooldown() {
		return this.cooldown;
	}

	@Override
	public boolean isSneakAbility() {
		return true;
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	public int getRange() {
		return this.selectRange;
	}

	public void setRange(final int range) {
		this.selectRange = range;
	}

	public int getHeight() {
		return this.height;
	}

	public void setHeight(final int height) {
		this.height = height;
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(final int width) {
		this.width = width;
	}

	public void setCooldown(final long cooldown) {
		this.cooldown = cooldown;
	}

	public void setLocation(final Location location) {
		this.location = location;
	}

	public int getSelectRange() {
		return this.selectRange;
	}

	public void setSelectRange(final int selectRange) {
		this.selectRange = selectRange;
	}

}
