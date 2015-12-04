-module(projectx_app).

-behaviour(application).

%% Application callbacks
-export([start/2, stop/1, init/0, get_tweets/2, test_post/1]).

%% ===================================================================
%% Application callbacks
%% ===================================================================

start(_StartType, _StartArgs) ->
    projectx_sup:start_link().

stop(_State) ->
    ok.
% Start the application
% erl -pa deps/*/ebin -pa ebin -s crypto
% projectx_app:init()
init() ->
  ssl:start(),
  register(tweet, spawn_link(fun loop/0)),
  io:format("spawned process: ~p~n", [whereis(tweet)]).

loop() ->
  %io:format("loop started ~n"),
  receive
    {get_tweets_reply, P, A} ->
      spawn(fun () -> jiffy_decode(P, A) end),
      %io:format("loop spawned jiffy_decode ~n"),
     loop();
    {extracted_list, P, Value} ->
      spawn(fun () -> extract_text(P, Value, []) end),
      spawn(fun () -> extract_text_mapreduce(P, Value, []) end),
      %io:format("loop spawned extract_text~n"),
       loop();
    {extracted_data, P, X} ->
      P ! {extracted_list_complete, X},
    loop()

    end.

% This function takes a list of strings, separates each word to a single term
% and then maps them like {"word", 1}
% Returns a proplist with the words reduced
map(List) ->
  % Splits a string into a comma separated list
  X = re:split(List,"[ ]",[{return,list}]),
  Map = [{string:to_lower(W), 1}|| W <- X],
  Result = dict:to_list(lists:foldl(fun reduce/2, dict:new(), Map)),
  %io:format("map Result: ~p~n", [Result]),
  ok.

% Sums the values of a map
reduce({Key, Value}, Sums) ->
  % Key, Fun, Initial Value, Dictionary
  dict:update(Key, fun (Old) -> Old + Value end, Value, Sums).

% This function will take the value of 'Query' and perform a search on
% twitter, returns JSONobjects as they come from Twitter servers
get_tweets(Query, Location) ->
  % Start ibrowse which we use for our network connections.
  ibrowse:start(),
  % Get Bearer token from twitter which we need for Application Auth
  BearerToken = app_auth(),
  % Construct the HTTP request with authentication and search parameters
  HeaderAuth = [{"Authorization","Bearer " ++ BearerToken}],
  URL = url_creator(Query, Location),
  % Request sent to Twitter
  {ok,_,_,TweetData} = ibrowse:send_req(URL, HeaderAuth, get),
  % Send answer to the server
  tweet ! {get_tweets_reply, self(), TweetData},
  % Wait for an answer that has gone through the entire process
  receive
    {extracted_list_complete, X} ->
      X
  end.

url_creator(Query, undefined) ->
  URIQuery  = http_uri:encode(Query),
  string:concat(string:concat(
          "https://api.twitter.com/1.1/search/tweets.json?q=",URIQuery),
            "&count=15&lang=en&result_type=recent");
url_creator(Query, Location) ->
  URIQuery  = http_uri:encode(Query),
  io:format("url_creator Location: ~p~n", [Location]),
  string:concat(string:concat(string:concat(
          "https://api.twitter.com/1.1/search/tweets.json?q=",URIQuery),
            "&count=15&lang=en&result_type=recent&geocode="), Location).

% Takes a JSON object and makes it more readable.
jiffy_decode(P, A) ->
  TweetDataDecoded = jiffy:decode(A),
  {TDD} = TweetDataDecoded, % extracts list from first tuple
  {_Key, Value} = lists:keyfind(<<"statuses">>, 1, TDD), % extracts the only tuple "statuses" from list

  %io:format("jiffy_decode: ~p~n", [Value]),
  tweet! {extracted_list, P, Value}.


% Extracts necessary values from JSON.
extract_text(P, [], Data) -> tweet ! {extracted_data, P, Data};
extract_text(P, Value, Data) ->
  %io:format("extract_text Value: ~p~n", [Value]),
  %io:format("extract_text Data: ~p~n", [Data]),
  {Head} = hd(Value), % extracts first tuple from list Value and extracts list from this tuple
  {_TKey1, TValue1} = lists:keyfind(<<"user">>, 1, Head), % extracts tuple "text" from list TLMet
  {TText} = TValue1,
  {_NKey, Name} = lists:keyfind(<<"name">>, 1, TText),
  %io:format("User name: ~p~n", [Name]),
  {_TKey, TValue} = lists:keyfind(<<"text">>, 1, Head),
  %io:format("Text: ~p~n", [TValue]),
  extract_text(P, tl(Value), Data ++ [Name] ++ [TValue]). % loop

% Extracts only the fields with "text" from JSON.
% Data is a proplist
extract_text_mapreduce(_P, [], Data) -> map(Data);
extract_text_mapreduce(P, Value, Data) ->
  % Extracts first tuple from the list Value
  {Head} = hd(Value),
  {_Key, Text} = lists:keyfind(<<"text">>, 1, Head),
  %io:format("Text: ~p~n", [TValue]),
  extract_text_mapreduce(P, tl(Value), Data ++ [Text]). % loop

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

test_post(Query) ->
  % Start ibrowse which we use for our network connections.
  ibrowse:start(),

  _URIQuery = http_uri:encode(Query),

  % Construct the HTTP request with authentication and search parameters
  Header = [{"Content-Type", "application/x-www-form-urlencoded"}],
  Body = "query=hockey",
  URL = "http://localhost:8080/findtweets",

  % Request sent to Twitter
  ibrowse:send_req(URL, Header, post, Body).
