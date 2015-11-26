%% @author Mochi Media <dev@mochimedia.com>
%% @copyright mochix Mochi Media <dev@mochimedia.com>

%% @doc Callbacks for the mochix application.

-module(mochix_app).
-author("Mochi Media <dev@mochimedia.com>").

-behaviour(application).
-export([start/2,stop/1]).


%% @spec start(_Type, _StartArgs) -> ServerRet
%% @doc application start callback for mochix.
start(_Type, _StartArgs) ->
    mochix_deps:ensure(),
    mochix_sup:start_link().

%% @spec stop(_State) -> ServerRet
%% @doc application stop callback for mochix.
stop(_State) ->
    ok.
