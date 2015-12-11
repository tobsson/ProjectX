-module(tweet_app).
-behaviour(gen_server).
-define(SERVER, ?MODULE).


%% NOTE: This module needs Couchbeam to be available to work with CouchDb database

%% ------------------------------------------------------------------
%% API Function Exports
%% ------------------------------------------------------------------

-export([start_link/0, word_val/1,text_val/1]).

%% ------------------------------------------------------------------
%% gen_server Function Exports
%% ------------------------------------------------------------------

-export([init/1, handle_call/3, handle_cast/2, handle_info/2,
         terminate/2, code_change/3]).

%% ------------------------------------------------------------------
%% API Function Definitions
%% ------------------------------------------------------------------

%Starts up the gen_server
start_link() ->
    gen_server:start_link({local, ?SERVER}, ?MODULE, [], []).

%Function to evaluate 1 word
word_val(Word) ->
    gen_server:call(            
      ?SERVER,                 
      {wordval, Word}).

% Function to evaluate a whole text
text_val(Text) ->
    gen_server:call(            
      ?SERVER,                 
      {textval, Text}).
    
%% ------------------------------------------------------------------
%% gen_server Function Definitions
%% ------------------------------------------------------------------

%Starts couchbeam upon gen_server startup
init([]) ->
    couchbeam:start(),
	{ok, []}.
  
  
%Call from word_val/1 function
%Replies with pos, neg, neutral score for one word
handle_call({wordval, Word}, _From, State) ->
    Points = word_Eval(Word),
	{reply, Points, State};	

% Call from text_val/1
% Replies with a score for a text, pos, neg or neutral
handle_call({textval,Text}, _From, State) ->
    Points = text_Eval(Text),
	{reply, Points, State}.	


handle_cast(_Msg, State) ->
    {noreply, State}.

handle_info(_Info, State) ->
    {noreply, State}.

terminate(_Reason, _State) ->
    ok.

code_change(_OldVsn, State, _Extra) ->
    {ok, State}.

%% ------------------------------------------------------------------
%% Internal Function Definitions
%% ------------------------------------------------------------------


% Function for making the query options usable with couchbeams query to CouchDB view
% The "options" for searching for rows in a view containing ONE specific key
% takes the form [{key, OPTIONS}] where OPTIONS is a binary() of the key, that is a word

 make_Options(Word) ->
  SearchWord = list_to_binary(Word),
  [{key, SearchWord}].

 
% Function evaluates a word against the "wordlist" document's "words_key_val"-view in CouchDB
% It makes a connection and evaluates the Word which is sent to the function and return -1, 1 or 0.

  word_Eval(Word) ->
    Url = "localhost:5984",
    Options = [],
    DesignName = "posneg", %The name for the DesignDocument in wordlist-database specifying the view
    ViewName = "words_key_val", % The actual view
	S = couchbeam:server_connection(Url, Options),  % connect to the server
    {ok, Db}=couchbeam:open_db(S, "wordlist", Options), % opening the "wordlist" database
	Options2 = make_Options(Word),
    {ok,ViewResults} = couchbeam_view:fetch(Db, {DesignName, ViewName},Options2), % returns rows corresponding to Word sent to function
	
	% Case statement to evaluate if we get a key and value form DB or an empty list(when the word isn't in our wordlist)
	% giving a score fomr the value if a key is found in db otherwise it returns "0".
	
	  case ViewResults of
	    [_] ->
	      {Value}=hd(ViewResults),
	      ValueTuple = lists:keyfind(<<"value">>, 1, Value),
	      {_, X} = ValueTuple,
	      X;
	    []-> 0
	  
	  end.
	
	
% Function to evaluate a whole text, splits the text's words into a list separating by spaces
% And make use of eval_word/1 for each word
% Function returns a total score for the received Text calculated by the sum of scores for each word in text

  text_Eval(Text) ->
    Tokens = string:tokens(Text, " "), % split by spaces into list
    PointsList=[word_Eval(N) || N <- Tokens], %Create a list with all scores for each word
    Total = sum(PointsList),      % summing the list
    io:format("The list of points:~p~n", [PointsList]), % JUST TEST to see what the scores for each words are
      if
       Total == 0 ->  0;
       Total <  0 -> -1;
       Total <  0 -> -1
   
      end.
  
	
% Simple sum-function to sum the list
	
  sum(L) -> 
     sum(L, 0).

  sum([H|T], Acc) -> 
     sum(T, H + Acc); 

  sum([], Acc) ->
     Acc.
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
