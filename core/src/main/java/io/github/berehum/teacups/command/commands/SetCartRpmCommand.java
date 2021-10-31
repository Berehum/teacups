package io.github.berehum.teacups.command.commands;

import cloud.commandframework.arguments.standard.BooleanArgument;
import cloud.commandframework.arguments.standard.FloatArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.context.CommandContext;
import io.github.berehum.teacups.TeacupsMain;
import io.github.berehum.teacups.attraction.Teacup;
import io.github.berehum.teacups.attraction.components.Cart;
import io.github.berehum.teacups.attraction.components.CartGroup;
import io.github.berehum.teacups.command.CommandManager;
import io.github.berehum.teacups.command.TeacupCommand;
import io.github.berehum.teacups.command.arguments.CartArgument;
import io.github.berehum.teacups.command.arguments.CartGroupArgument;
import io.github.berehum.teacups.command.arguments.TeacupArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;

public class SetCartRpmCommand extends TeacupCommand {

    public SetCartRpmCommand(final @NonNull TeacupsMain plugin, final @NonNull CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        this.commandManager.registerSubcommand(builder ->
                builder.literal("setrpm").literal("cart")
                        .argument(TeacupArgument.of("teacup"))
                        .argument(CartGroupArgument.of("cartgroup"))
                        .argument(CartArgument.of("cart"))
                        .argument(IntegerArgument.of("rpm"))
                        .argument(BooleanArgument.optional("add to existing"))
                        .handler(this::setRpm)
        );
    }

    private void setRpm(final @NonNull CommandContext<CommandSender> context) {
        final Teacup teacup = context.get("teacup");
        final CartGroup cartgroup = context.get("cartgroup");
        final Cart cart = context.get("cart");
        final int rpm = context.get("rpm");
        final Optional<Boolean> addToExisting = context.getOptional("add to existing");

        if (addToExisting.isPresent() && addToExisting.get()) {
            cart.setRpm(cart.getRpm() + rpm);
        } else {
            cart.setRpm(rpm);
        }

        context.getSender().sendMessage(ChatColor.GREEN + "You've set the rpm of " + cart.getId() + " in "
                + cartgroup.getId() + " in " + teacup.getId() + " to " + cart.getRpm());
    }
}
