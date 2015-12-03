%% @author Mochi Media <dev@mochimedia.com>
%% @copyright 2010 Mochi Media <dev@mochimedia.com>

%% @doc Web server for mochix.

-module(mochix_web).
-author("Mochi Media <dev@mochimedia.com>").

-export([start/1, stop/0, loop/2]).

%% External API

start(Options) ->
    {DocRoot, Options1} = get_option(docroot, Options),
    Loop = fun (Req) ->
                   ?MODULE:loop(Req, DocRoot)
           end,
    mochiweb_http:start([{name, ?MODULE}, {loop, Loop} | Options1]).

stop() ->
    mochiweb_http:stop(?MODULE).

loop(Req, DocRoot) ->
    "/" ++ Path = Req:get(path),
    try
        case Req:get(method) of
            Method when Method =:= 'GET'; Method =:= 'HEAD' ->
                case Path of
                  "hello_world" ->
                    Req:respond({200, [{"Content-Type", "text/plain"}],
                    "Hello world!\n"});
                  % Takes all requests to /findtweets and sends them to
                  % a function.
                  % .../findtweets?query=hockey
                  "findtweets" -> spawn(fun () -> findget(Req) end);
                  _ ->
                    Req:respond({200, [{"Content-Type", "text/plain"}],
                    "You are doing it wrong!\n"})
                end;
            'POST' ->
                case Path of
                  "findtweets" -> spawn(fun () -> findpost(Req) end);
                    _ ->
                      Req:respond({200, [{"Content-Type", "text/plain"}],
                      "You are doing it wrong!\n"})
                end;
            _ ->
                Req:respond({501, [], []})
        end
    catch
        Type:What ->
            Report = ["web request failed",
                      {path, Path},
                      {type, Type}, {what, What},
                      {trace, erlang:get_stacktrace()}],
            error_logger:error_report(Report),
            Req:respond({500, [{"Content-Type", "text/plain"}],
                         "request failed, sorry\n"})
    end.

%% Internal API
get_option(Option, Options) ->
    {proplists:get_value(Option, Options), proplists:delete(Option, Options)}.

% Takes a query from an HTTP request and gives back an answer encoded as JSON
findget(Req) ->
  QueryStringData = Req:parse_qs(),
  Query           = proplists:get_value("query", QueryStringData),
  Search          = projectx_app:tweet_search(Query),
  HTMLoutput      = mochijson2:encode(Search),
  %io:format("loop founddata: ~p~n", [FoundData]),
  Req:respond({200, [{"Content-Type", "text/plain"}],
                HTMLoutput}).

findpost(Req) ->
  io:format("findpost started ~n"),
  io:format("findpost Req: ~p~n", [Req]),
  PostData  = Req:parse_post(),
  io:format("findpost postData: ~p~n", [PostData]),
  Query     = proplists:get_value("query", PostData),
  io:format("findpost query: ~p~n", [Query]),
  Search    = projectx_app:tweet_search(Query),
  HTMLoutput      = mochijson2:encode(Search),
  Req:respond({200, [{"Content-Type", "text/plain"}],
                HTMLoutput}).

%%
%% Tests
%%
-ifdef(TEST).
-include_lib("eunit/include/eunit.hrl").

you_should_write_a_test() ->
    ?assertEqual(
       "No, but I will!",
       "Have you written any tests?"),
    ok.

-endif.
