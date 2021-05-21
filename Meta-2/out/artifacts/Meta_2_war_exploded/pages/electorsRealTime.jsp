<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Real Time Electors</title>
    <link rel="stylesheet" type="text/css" href="style.css">
    <script type="text/javascript">

        var websocket = null;

        window.onload = function () {
            connect('ws://' + window.location.host + '/Meta_2_war_exploded/electorsWS');
            document.getElementById("electionName").onchange = electionSelectEvent;
        }

        function electionSelectEvent(event)  {
            websocket.send(document.getElementById('electionName').value);
        }

        function connect(host) { // connect to the host websocket
            if ('WebSocket' in window)
                websocket = new WebSocket(host);
            else if ('MozWebSocket' in window)
                websocket = new MozWebSocket(host);
            else {
                writeToHistory('Get a real browser which supports WebSocket.');
                return;
            }

            websocket.onopen    = onOpen;
            websocket.onclose   = onClose;
            websocket.onmessage = onMessage;
            websocket.onerror   = onError;
        }

        function onOpen(event) {
            writeToHistory('Connected to ' + window.location.host + '.');
            websocket.send(document.getElementById('electionName').value);
        }

        function onClose(event) {
            writeToHistory('WebSocket closed (code ' + event.code + ').');
            document.getElementById('electionName').onchange = null;
        }

        function onMessage(message) {
            writeToHistory(message.data);
        }

        function onError(event) {
            writeToHistory('WebSocket error.');
            document.getElementById('electionName').onchange = null;
        }

        function writeToHistory(text) {
            var history = document.getElementById('history');
            var line = document.createElement('p');
            line.style.wordWrap = 'break-word';
            line.innerHTML = text;
            history.appendChild(line);
            history.scrollTop = history.scrollHeight;
        }

    </script>
</head>
<body>
<h2>Real Time Electors</h2>
<noscript>JavaScript must be enabled for WebSockets to work.</noscript>
<div>
    <s:select id="electionName" list="watchableElections"/>
    <div id="container"><div id="history"></div></div>
</div>
</body>
</html>