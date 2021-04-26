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

import java.io.IOException;
import java.util.UUID;

public class LookupCommand {
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            LiteralCommandNode<ServerCommandSource> uuidRoot = CommandManager.literal("uuidlookup").then(CommandManager.argument("UUID", UuidArgumentType.uuid()).executes(context -> lookup(context, context.getArgument("UUID", UUID.class)))).build();
            LiteralCommandNode<ServerCommandSource> nameRoot = CommandManager.literal("uuidlookup").then(CommandManager.argument("Username", StringArgumentType.greedyString()).executes(context -> lookup(context, context.getArgument("Username", String.class)))).build();

            dispatcher.getRoot().addChild(uuidRoot);
            dispatcher.getRoot().addChild(nameRoot);
        });
    }

    private static Text copyToClipboard(String string) {
        return new LiteralText(string).styled(style -> {
            return style
                    .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, string))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.copy.click")))
                    .withInsertion(string);
        });
    }

    private static int lookup(CommandContext<ServerCommandSource> context, UUID uuid) {
        try {
            Text username = copyToClipboard(HTTPGet.getName(uuid));
            context.getSource().sendFeedback(new TranslatableText("command.uuidlookup.username", username), false);
        } catch (IOException e) {
            context.getSource().sendFeedback(new TranslatableText("command.uuidlookup.failure"), false);
        }
        return 1;
    }

    private static int lookup(CommandContext<ServerCommandSource> context, String string) {
        try {
            Text uuid = copyToClipboard(String.valueOf(HTTPGet.getUUID(string)));
            context.getSource().sendFeedback(new TranslatableText("command.uuidlookup.uuid", uuid), false);
        } catch (IOException e) {
            context.getSource().sendFeedback(new TranslatableText("command.uuidlookup.failure"), false);
        }
        return 1;
    }
}