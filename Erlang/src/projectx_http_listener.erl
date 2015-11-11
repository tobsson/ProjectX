- module(projectx_http_listener).

- export([start/0]).





start()->
 {ok, ListenSock}=gen_tcp:listen(8080, [list,{active, false},{packet,http}]),
 ?MODULE:loop(ListenSock).

loop(ListenSock) ->
 {ok, Data}=gen_tcp:accept(ListenSock),
 spawn(?MODULE, handle_request, [Data]),
 ?MODULE:loop(ListenSock).

 handle_request(Sock) ->
  {ok, {http_request, Method, Path, Version}}=gen_tcp:recv(Sock, 0),
  case (Method) of
   'POST' -> ok; %handle_post(Sock);
   _ -> io:format("what we got: ~p~n",[Sock])
  end.
