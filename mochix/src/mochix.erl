%% @author Mochi Media <dev@mochimedia.com>
%% @copyright 2010 Mochi Media <dev@mochimedia.com>

%% @doc mochix.

-module(mochix).
-author("Mochi Media <dev@mochimedia.com>").
-export([start/0, stop/0]).

ensure_started(App) ->
    case application:start(App) of
        ok ->
            ok;
        {error, {already_started, App}} ->
            ok
    end.


%% @spec start() -> ok
%% @doc Start the mochix server.
start() ->
    mochix_deps:ensure(),
    ensure_started(crypto),
    application:start(mochix).


%% @spec stop() -> ok
%% @doc Stop the mochix server.
stop() ->
    application:stop(mochix).
