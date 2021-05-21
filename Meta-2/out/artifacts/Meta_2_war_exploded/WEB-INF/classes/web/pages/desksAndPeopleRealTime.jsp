<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
	<head>
		<title>Real Time Desks And People</title>
		<link rel="stylesheet" type="text/css" href="style.css">
		<script type="text/javascript">

			var websocket = null;

			window.onload = async function () {
				connect('ws://' + window.location.host + '/Meta_2_war_exploded/desksAndPeopleWS');
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
			}

			function onClose(event) {
				writeToHistory('WebSocket closed (code ' + event.code + ').');
			}

			function onMessage(message) {
				writeToHistory(message.data);
			}

			function onError(event) {
				writeToHistory('WebSocket error.');
				document.getElementById('chat').onkeydown = null;
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
		<noscript>JavaScript must be enabled for WebSockets to work.</noscript>
		<h2>Real Time Desks And People</h2>
		<div>
			<div id="container"><div id="history"></div></div>
		</div>
	</body>
</html>