package io.github.projectet.uuidlookup.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.projectet.uuidlookup.util.HTTPGet;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.util.UUID;

public class LookupCommand {
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            LiteralCommandNode<ServerCommandSource> uuidRoot = CommandManager
                    .literal("uuidlookup")
                    .then(CommandManager
                            .argument("UUID", UuidArgumentType.uuid())
                            .executes(context -> lookup(context, context
                                    .getArgument("UUID", UUID.class))))
                    .build();

            LiteralCommandNode<ServerCommandSource> nameRoot = CommandManager
                    .literal("uuidlookup")
                    .then(CommandManager
                            .argument("Username", StringArgumentType.greedyString())
                            .executes(context -> lookup(context, context
                                    .getArgument("Username", String.class))))
                    .build();

            LiteralCommandNode<ServerCommandSource> Root = CommandManager
                    .literal("uuidlookup")
                    .executes(LookupCommand::help)
                    .build();

            dispatcher.getRoot().addChild(Root);
            dispatcher.getRoot().addChild(uuidRoot);
            dispatcher.getRoot().addChild(nameRoot);
        });
    }

    private static Text copyToClipboard(String string) {
        return new LiteralText(string).styled(style -> {
            return style
                    .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, string))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.copy.click")))
                    .withInsertion(string)
                    .withColor(Formatting.GREEN);
        });
    }

    private static int lookup(CommandContext<ServerCommandSource> context, UUID uuid) {
        try {
            Text username = copyToClipboard(HTTPGet.getName(uuid));
            context.getSource().sendFeedback(new TranslatableText("Username from given UUID is: %s", username), false);
        } catch (IOException e) {
            context.getSource().sendFeedback(new LiteralText("Error, input is malformed or uuid does not exist."), false);
        }
        return 1;
    }

    private static int lookup(CommandContext<ServerCommandSource> context, String string) {
        try {
            Text uuid = copyToClipboard(String.valueOf(HTTPGet.getUUID(string)));
            context.getSource().sendFeedback(new TranslatableText("UUID from given Username is: %s", uuid), false);
        } catch (IOException e) {
            context.getSource().sendFeedback(new LiteralText("Error, input is malformed or uuid does not exist."), false);
        }
        return 1;
    }

    private static int help(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(new LiteralText("Usage(s):"), false);
        context.getSource().sendFeedback(new LiteralText("/uuidlookup <UUID> - Returns a username from a given UUID"), false);
        context.getSource().sendFeedback(new LiteralText("/uuidlookup <Username> - Returns a UUID from a given Username"), false);
        return 1;
    }
}
