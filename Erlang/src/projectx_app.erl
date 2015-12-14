-module(projectx_app).

-behaviour(application).

%% Application callbacks
-export([start/2, stop/1, init/0, tweet_search/0]).

%% ===================================================================
%% Application callbacks
%% ===================================================================

start(_StartType, _StartArgs) ->
    projectx_sup:start_link().

stop(_State) ->
    ok.
% Start the application
% erl -pa deps/*/ebin -pa ebin -s crypto -s ssl
% projectx_app:init()
init() ->
  register(tweet, spawn_link(fun loop/0)),
  io:format("spawned process: ~p~n", [whereis(tweet)]),
  tweet_search().

loop() ->
  io:format("loop started ~n"),
  receive
    {tweet_search, Q} ->
      spawn(fun () -> get_tweets(Q) end),
      io:format("loop spawned get_tweets ~n"),
      loop();
    {get_tweets_reply, A} ->
      spawn(fun () -> jiffy_decode(A) end),
      io:format("loop spawned jiffy_decode ~n"),
     loop();
  %  {jiffy_decode_reply, TweetDataDecoded} ->
  %    spawn(fun () -> extract_info(TweetDataDecoded) end),
  %    io:format("loop spawned extract_info~n"),
  %    loop();
    {extracted_list, Value} ->
      spawn(fun () -> extract_text(Value) end),
      io:format("loop spawned extract_text~n"),
  %     loop();
  %    {extracted_list2, M2} ->
  %      spawn(fun () -> extract_text2(M2) end),
  %      io:format("loop spawned extract_text2~n"),
       loop()

    end.

% This function is for testing search queries in the terminal
tweet_search() ->
  % Gets user input from the shell doesn't seem to ever evaluate to anything
  % but Data
  X = string:strip(io:get_line("Search term: "), right, $\n),
  io:format("io:get_line: ~p~n", [X]),
  case X of
    {error, Reason} ->
      io:format("tweet_search experienced an error: ~p~n", [Reason]),
      tweet_search();
    eof ->
      io:format("tweet_search reached end of line ~n"),
      tweet_search();
    Data ->
      tweet ! {tweet_search, Data},
      %io:format("tweet_search bang: ~n"),
      tweet_search()
  end.

% This function will take the value of 'Query' and perform a search on
% twitter, returns JSONobjects as they come from Twitter servers
get_tweets(Query) ->
  % Start ibrowse which we use for our network connections.
  ibrowse:start(),

  % Get Bearer token from twitter which we need for Application Auth
  BearerToken = app_auth(),
  %io:format("get_ tweets: BearerToken: ~p~n", [BearerToken]),

  URIQuery = http_uri:encode(Query),

  % Construct the HTTP request with authentication and search parameters
  HeaderAuth = [{"Authorization","Bearer " ++ BearerToken}],
  URL = string:concat(string:concat(
          "https://api.twitter.com/1.1/search/tweets.json?q=",URIQuery),
            "&count=2&lang=en"),

  % Request sent to Twitter
  {ok,_,_,TweetData} = ibrowse:send_req(URL, HeaderAuth, get),

  % Send answer to the server
  tweet ! {get_tweets_reply, TweetData}.


% Takes a JSON object and makes it more readable.
jiffy_decode(A) ->
  TweetDataDecoded = jiffy:decode(A),
  {TDD} = TweetDataDecoded, % extracts list from first tuple
  {_Key, Value} = lists:keyfind(<<"statuses">>, 1, TDD), % extracts the only tuple "statuses" from list


  tweet! {extracted_list, Value}.


% Extracts necessary values from JSON.
extract_text([]) -> ok;
extract_text(Value) ->
  {Head} = hd(Value), % extracts first tuple from list Value and extracts list from this tuple
  {_TKey1, TValue1} = lists:keyfind(<<"user">>, 1, Head), % extracts tuple "text" from list TLMet
  {TText} = TValue1,
  {_NKey, Name} = lists:keyfind(<<"name">>, 1, TText),
  io:format("User name: ~p~n", [Name]),
  {_TKey, TValue} = lists:keyfind(<<"text">>, 1, Head),
  io:format("Text: ~p~n", [TValue]),
  extract_text(tl(Value)). % loop

% This function returns a Bearer Token from Twitter
% that's needed for Application Authentication
app_auth() ->
  % ConsumerKey and Secret are specific to this an app and can be found here:
  % https://apps.twitter.com/
  % This function will encode the values, construct the HTTP request and
  % return a Bearer Token as a list
  ConsumerKey     = "JNEVsG01mMdYIKq404MMmXw2H",
  ConsumerSecret  = "aeNjhFV0F02PjNktQEBN2ESGbbzbDlcNeVufBxsKI9B0qFjFhB",
  EncodedConsumerKey    = http_uri:encode(ConsumerKey),
  EncodedConsumerSecret = http_uri:encode(ConsumerSecret),
  ReadyToBase = string:concat(
    string:concat(EncodedConsumerKey, ":"), EncodedConsumerSecret),
  Based   = base64:encode_to_string(ReadyToBase),
  Body    = "grant_type=client_credentials",
  Headers = [{"Authorization","Basic " ++ Based},
              {"Content-Type","application/x-www-form-urlencoded;charset=UTF-8"}],
  % Request the Bearer Token
  {ok,_,_,TokenAnswer} = ibrowse:send_req(
                            "https://api.twitter.com/oauth2/token",
                                Headers, post, Body),

  % Turn the answer into something more readable
  {TokenDecoded}    = jiffy:decode(TokenAnswer),
  %io:format("app_auth: BearerAnswer: ~p~n", [TokenDecoded]),
  % Check that it really is a bearer token
  %{_,TokenType}     = lists:keyfind(<<"token_type">>, 1, TokenDecoded),
  % Extract the Token and put it into a list.
  {_,BearerToken}   = lists:keyfind(<<"access_token">>, 1, TokenDecoded),
  BearerTokenString = binary:bin_to_list(BearerToken),
  BearerTokenString.
