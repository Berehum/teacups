package io.github.berehum.teacups.attraction;

import io.github.berehum.teacups.attraction.components.Cart;
import io.github.berehum.teacups.attraction.components.CartGroup;
import io.github.berehum.teacups.attraction.components.Seat;
import io.github.berehum.teacups.utils.CustomConfig;
import io.github.berehum.teacups.utils.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class Teacup {

    private final CustomConfig customConfig;

    private final String id;

    private final double radius;
    private final Location location;
    private final Map<String, CartGroup> cartGroups = new HashMap<>();

    private float rpm = 0.0F;
    private double rotation = 0.0;
    private boolean locked = false;

    public Teacup(String id, CustomConfig customConfig) {
        this.id = id;
        this.customConfig = customConfig;
        FileConfiguration config = customConfig.getConfig();
        this.location = new Location(Bukkit.getWorld(config.getString("settings.location.world")),
                config.getDouble("settings.location.x"), config.getDouble("settings.location.y"), config.getDouble("settings.location.z"));

        this.radius = config.getDouble("settings.radius");

        ConfigurationSection groupSection = config.getConfigurationSection("cartgroups");
        if (groupSection == null) return;
        for (String groupKey : groupSection.getKeys(false)) {

            ConfigurationSection cartsSection = groupSection.getConfigurationSection(groupKey + ".carts");
            if (cartsSection == null) continue;
            Map<String, Cart> carts = new HashMap<>();

            for (String cartKeys : cartsSection.getKeys(false)) {
                List<Seat> seats = new ArrayList<>();
                for (int i = 0; i < cartsSection.getInt(cartKeys + ".seats"); i++) {
                    seats.add(new Seat(location));
                }

                carts.put(cartKeys, new Cart(cartKeys, location, cartsSection.getDouble(cartKeys + ".radius"), seats));
            }

            cartGroups.put(groupKey, new CartGroup(groupKey, location, groupSection.getDouble(groupKey + ".radius"), carts));

        }

    }

    public boolean init() {
        if (cartGroups.isEmpty() || location == null) return false;
        List<CartGroup> cartGroups = new ArrayList<>(this.cartGroups.values());
        for (int i = 0; i < cartGroups.size(); i++) {
            CartGroup group = cartGroups.get(i);
            group.setLocation(MathUtils.drawPoint(location, radius, i, cartGroups.size(), rotation));
            group.init();
        }
        updateChildLocations();
        return true;
    }

    public void disable() {
        cartGroups.values().forEach(CartGroup::disable);
    }


    public String getId() {
        return id;
    }


    public Set<Player> getPlayersOnRide() {
        Set<Player> players = new HashSet<>();
        cartGroups.values().forEach(cartGroup -> players.addAll(cartGroup.getPlayersInCartGroup()));
        return players;
    }

    public Map<String, CartGroup> getCartGroups() {
        return cartGroups;
    }

    public List<Seat> getSeats() {
        List<Seat> seats = new ArrayList<>();
        cartGroups.values().forEach(cartGroup -> seats.addAll(cartGroup.getSeats()));
        return seats;
    }

    public Location getLocation() {
        return location;
    }

    public void updateChildLocations() {
        List<CartGroup> cartGroups = new ArrayList<>(this.cartGroups.values());
        for (int i = 0; i < cartGroups.size(); i++) {
            CartGroup group = cartGroups.get(i);
            group.setLocation(MathUtils.drawPoint(location, radius, i, cartGroups.size(), rotation));
            group.updateChildLocations();
        }
    }

    public double getRadius() {
        return radius;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation % (2 * Math.PI);
    }

    public float getRpm() {
        return rpm;
    }

    public void setRpm(float rpm) {
        this.rpm = rpm;
    }

    public CustomConfig getCustomConfig() {
        return customConfig;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        if (this.locked == locked) return;
        this.locked = locked;
        getSeats().forEach(seat -> seat.setLocked(locked));
    }
}
