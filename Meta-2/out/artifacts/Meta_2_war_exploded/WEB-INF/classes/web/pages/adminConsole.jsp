<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
	<head>
		<title>Admin Console</title>
	</head>
	<body>
		<h2>Admin Console</h2>
		<p>Welcome, ${sessionScope.get('username')}.</p>

		<h3>People</h3>
		<s:a action="signUpMenu"><button>Sign up</button></s:a>

		<h3>Database Info</h3>
		<s:a action="databaseInfo"><button>Database Info</button></s:a>

		<h3>Elections</h3>
		<s:a action="createElectionMenu"><button>Create Election</button></s:a>
		<s:a action="editElectionMenu"><button>Edit Election</button></s:a>
		<s:a action="endedElectionLogMenu"><button>Ended Elections Log</button></s:a>
		<s:a action="personAuditMenu"><button>Person Audit</button></s:a>

		<h3>Lists</h3>
		<s:a action="createListMenu"><button>Create List</button></s:a>
		<s:a action="addListToElection"><button>Add List to Election</button></s:a>
		<s:a action="removeListFromElection"><button>Remove List from Election</button></s:a>
		<s:a action="addPeopleToList"><button>Add People to List </button></s:a>
		<s:a action="removePeopleFromList"><button>Remove People from List </button></s:a>

		<h3>Real Time Data</h3>
		<s:a action="desksAndPeopleRealTime"><button>Desks and People</button></s:a>
		<s:a action="electorsRealTime"><button>Elections</button></s:a>
	</body>
</html>
