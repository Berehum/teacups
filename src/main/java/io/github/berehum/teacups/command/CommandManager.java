package io.github.berehum.teacups.command;

import cloud.commandframework.Command;
import cloud.commandframework.brigadier.CloudBrigadierManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.captions.CaptionRegistry;
import cloud.commandframework.captions.FactoryDelegatingCaptionRegistry;
import cloud.commandframework.exceptions.ArgumentParseException;
import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.exceptions.parsing.ParserException;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.types.Func;
import io.github.berehum.teacups.TeacupsMain;
import io.github.berehum.teacups.command.arguments.CartArgument;
import io.github.berehum.teacups.command.arguments.CartGroupArgument;
import io.github.berehum.teacups.command.arguments.ShowArgument;
import io.github.berehum.teacups.command.arguments.TeacupArgument;
import io.github.berehum.teacups.command.commands.*;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.units.qual.N;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public final class CommandManager extends PaperCommandManager<CommandSender> {

    private final BukkitAudiences bukkitAudiences;

    public CommandManager(final @NonNull TeacupsMain plugin) throws Exception {
        super(plugin, CommandExecutionCoordinator.simpleCoordinator(), UnaryOperator.identity(), UnaryOperator.identity());

        this.bukkitAudiences = BukkitAudiences.create(plugin);

        if (this.queryCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            this.registerBrigadier();
            final CloudBrigadierManager<?, ?> brigManager = this.brigadierManager();
            if (brigManager != null) {
                brigManager.setNativeNumberSuggestions(false);
            }
        }

        new TeacupCaptionRegistry<>();

        registerExceptions();

        if (this.queryCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            this.registerAsynchronousCompletions();
        }

        ImmutableList.of(
                new HelpCommand(plugin, this),
                new SpawnCommand(plugin, this),
                new ReloadCommand(plugin, this),
                new LockCommand(plugin, this),
                new KickCommand(plugin, this),
                new ActionCommand(plugin, this),
                new ExecuteCommand(plugin, this),
                new SetRpmCommand(plugin, this)
        ).forEach(TeacupCommand::register);

    }

    public void registerExceptions() {
        new MinecraftExceptionHandler<CommandSender>()
                .withArgumentParsingHandler()
                .withInvalidSenderHandler()
                .withInvalidSyntaxHandler()
                .withNoPermissionHandler()
                .withCommandExecutionHandler()
                .apply(this, this.bukkitAudiences::sender);


        setCaptionRegistry(new TeacupCaptionRegistry<>());

    }

    public void registerSubcommand(UnaryOperator<Command.Builder<CommandSender>> builderModifier) {
        this.command(builderModifier.apply(this.rootBuilder()));
    }

    private Command.@NonNull Builder<CommandSender> rootBuilder() {
        return this.commandBuilder("teacup", "Teacups")
                .meta(CommandMeta.DESCRIPTION, "Teacups command. '/teacup help'");
    }

    public BukkitAudiences getBukkitAudiences() {
        return bukkitAudiences;
    }

}
