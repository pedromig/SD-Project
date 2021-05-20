package core;

public interface Configuration {

    /* Custom Action Return Values */
    String USER = "user";
    String ADMIN = "admin";

    /* Connection Settings */
    String IP = "localhost";
    String PORT = "7000";
    String SERVER_NAME = "RmiServer";

    /* Login Settings */
    String ADMIN_USERNAME = "admin";
    String ADMIN_PASSWORD = "sudo";

    /* Admin Session Configs */
    String STUDENT = "Student";
    String TEACHER = "Teacher";
    String EMPLOYEE = "Employee";
    String STRUTS_DATE_FORMAT = "yyyy-MM-dd";
    String STRUTS_TIME_FORMAT = "HH:mm";

    /* Session Map Keys */
    String RMI_CONNECTOR_KEY = "rmiConnector";
    String SERVER_STATUS_KEY = "rmiServerOnline";

    String USERNAME_KEY = "username";
    String PASSWORD_KEY = "password";
    String ADMIN_MODE_KEY = "isAdmin";

    String ELECTIONS_PRINT_KEY = "elections";
    String LISTS_PRINT_KEY = "lists";
    String PEOPLE_PRINT_KEY = "people";

    String ENDED_ELECTIONS_LOG_KEY = "endedElectionsLog";
    String PEOPLE_AUDIT_KEY = "peopleAudit";
    String SELECTABLE_PEOPLE_AUDIT_KEY = "selectablePeopleAudit";

    String SELECTABLE_PEOPLE_KEY_ADD_LE = "selectablePeopleKeyAddLE";
    String SELECTABLE_PEOPLE_KEY_ADD_PL = "selectablePeopleKeyAddPL";
    String SELECTABLE_PEOPLE_KEY_REM_LE = "selectablePeopleKeyRemLE";
    String SELECTABLE_PEOPLE_KEY_REM_PL = "selectablePeopleKeyRemPL";

    String SELECTABLE_ELECTIONS_KEY_ADD_LE = "selectableElectionsKeyAddLE";
    String SELECTABLE_ELECTIONS_KEY_ADD_PL = "selectableElectionsKeyAddPL";
    String SELECTABLE_ELECTIONS_KEY_REM_LE = "selectableElectionsKeyRemLE";
    String SELECTABLE_ELECTIONS_KEY_REM_PL = "selectableElectionsKeyRemPL";

    String SELECTABLE_LISTS_KEY_ADD_LE = "selectableListsKeyAddLE";
    String SELECTABLE_LISTS_KEY_ADD_PL = "selectableListsKeyAddPL";
    String SELECTABLE_LISTS_KEY_REM_LE = "selectableListsKeyRemLE";
    String SELECTABLE_LISTS_KEY_REM_PL = "selectableListsKeyRemPL";

    String SELECTED_PERSON_KEY_ADD_PL = "selectedPersonKeyAddPL";
    String SELECTED_PERSON_KEY_REM_PL = "selectedPersonKeyRemPL";

    String SELECTED_ELECTION_KEY_ADD_LE = "selectedElectionKeyAddLE";
    String SELECTED_ELECTION_KEY_REM_LE = "selectedElectionKeyRemLE";

    String SELECTED_LIST_KEY_ADD_LE = "selectedListKeyAddLE";
    String SELECTED_LIST_KEY_ADD_PL = "selectedListKeyAddPL";
    String SELECTED_LIST_KEY_REM_LE = "selectedListKeyRemLE";
    String SELECTED_LIST_KEY_REM_PL = "selectedListKeyRemPL";

    String SELECTED_ELECTION_EDIT = "selectedElectionEdit";
    String SELECTABLE_ELECTIONS_EDIT = "selectableElectionsEdit";

    String SELECTABLE_DEPARTMENTS_ADD = "selectableDepartmentsAdd";
    String SELECTABLE_DEPARTMENTS_ADD_REST = "selectableDepartmentsAddRest";
    String SELECTABLE_DEPARTMENTS_REM = "selectableDepartmentsRem";
    String SELECTABLE_DEPARTMENTS_REM_REST = "selectableDepartmentsRemRest";

    String SELECTABLE_VOTING_ELECTIONS_KEY = "votingSelectableElectionsKey";
    String SELECTED_VOTING_ELECTION_KEY = "selectedVotingElectionKey";
    String SELECTABLE_VOTING_LISTS_KEY = "votingSelectableListsKey";



}
