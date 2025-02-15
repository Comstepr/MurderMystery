package plugily.projects.murdermystery.handlers.hologram;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArmorStandHologram {

  private Item entityItem;
  private ItemStack item;
  private List<String> lines = new ArrayList<>();
  private Location location;

  private final List<ArmorStand> armorStands = new ArrayList<>();

  public ArmorStandHologram() {
  }

  public ArmorStandHologram(Location location) {
    this.location = location;
  }

  public ArmorStandHologram(Location location, @NotNull String... lines) {
    this.location = location;
    this.lines = Arrays.asList(lines);

    append();
  }

  public ArmorStandHologram(Location location, @NotNull List<String> lines) {
    this.location = location;
    this.lines = lines;

    append();
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public ItemStack getItem() {
    return item;
  }

  public Item getEntityItem() {
    return entityItem;
  }

  @NotNull
  public List<String> getLines() {
    return lines;
  }

  @NotNull
  public List<ArmorStand> getArmorStands() {
    return armorStands;
  }

  public ArmorStandHologram appendLines(@NotNull String... lines) {
    this.lines = Arrays.asList(lines);
    append();
    return this;
  }

  public ArmorStandHologram appendLines(@NotNull List<String> lines) {
    this.lines = lines;
    append();
    return this;
  }

  public ArmorStandHologram appendLine(@NotNull String line) {
    this.lines.add(line);
    append();
    return this;
  }

  public ArmorStandHologram appendItem(@NotNull ItemStack item) {
    this.item = item;
    append();
    return this;
  }

  public void delete() {
    for(ArmorStand armor : armorStands) {
      armor.setCustomNameVisible(false);
      armor.remove();
      HologramManager.getArmorStands().remove(armor);
    }
    if(entityItem != null) {
      entityItem.remove();
    }
    armorStands.clear();
  }

  public boolean isDeleted() {
    return entityItem == null && armorStands.isEmpty();
  }

  private void append() {
    delete();
    double distanceAbove = -0.27, y = location.getY();

    for(int i = 0; i <= lines.size() - 1; i++) {
      y += distanceAbove;
      ArmorStand eas = getEntityArmorStand(location, y);
      eas.setCustomName(lines.get(i));
      armorStands.add(eas);
      HologramManager.getArmorStands().add(eas);
    }

    if(item != null && item.getType() != org.bukkit.Material.AIR) {
      Location l = location.clone();
      entityItem = location.getWorld().dropItem(l, item);
      if(Bukkit.getServer().getVersion().contains("Paper"))
        entityItem.setCanMobPickup(false);
      entityItem.setCustomNameVisible(false);
      if(ServerVersion.Version.isCurrentHigher(ServerVersion.Version.v1_8_R3)) {
        entityItem.setGravity(true);
        entityItem.setInvulnerable(true);
      }
      entityItem.teleport(l);
    }
  }

  /**
   * @param y the y axis of the hologram
   * @return {@link ArmorStand}
   */
  private ArmorStand getEntityArmorStand(Location loc, double y) {
    loc.setY(y);
    if(location != null) {
      location.getWorld().getNearbyEntities(location, 0.2, 0.2, 0.2).forEach(entity -> {
        if(entity instanceof ArmorStand && !armorStands.contains(entity) && !HologramManager.getArmorStands().contains(entity)) {
          entity.remove();
          entity.setCustomNameVisible(false);
          HologramManager.getArmorStands().remove(entity);
        }
      });
    }
    ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
    stand.setVisible(false);
    stand.setGravity(false);
    stand.setCustomNameVisible(true);
    return stand;
  }
}
