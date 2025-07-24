package org.axolotlj.serverguard.command.argument;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.commands.CommandSourceStack;

import org.axolotlj.serverguard.list.blacklist.NameBlacklistManager;
import org.axolotlj.serverguard.list.whitelist.IPWhitelistManager;
import org.axolotlj.serverguard.list.whitelist.UUIDWhitelistManager;

public class SuggestionProviders {

    public static final SuggestionProvider<CommandSourceStack> IP_SUGGESTIONS = (context, builder) ->
        suggestFrom(IPWhitelistManager.getInstance().list(), builder);

    public static final SuggestionProvider<CommandSourceStack> UUID_SUGGESTIONS = (context, builder) ->
        suggestFrom(UUIDWhitelistManager.getInstance().list(), builder);

    public static final SuggestionProvider<CommandSourceStack> NAMEBLACKLIST_SUGGESTIONS = (context, builder) ->
        suggestFrom(NameBlacklistManager.getInstance().list(), builder);

    public static final SuggestionProvider<CommandSourceStack> ONLINE_PLAYERS = (context, builder) -> {
        var server = context.getSource().getServer();
        var names = server.getPlayerList().getPlayers().stream()
            .map(player -> player.getGameProfile().getName())
            .toList();
        return suggestFrom(names, builder);
    };

    private static CompletableFuture<Suggestions> suggestFrom(Iterable<String> list, SuggestionsBuilder builder) {
        list.forEach(builder::suggest);
        return builder.buildFuture();
    }
}
