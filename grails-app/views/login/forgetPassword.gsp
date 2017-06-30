<html>
<head>
    <meta name="layout" content="main"/>
</head>
<body>
    <g:form controller="login" action="sendPassword">
        Email ID : <g:textField name='username' size='25' labelCodeDefault='Username'/>
        <g:submitButton name="submit" value="Send" />
    </g:form>
</body>
</html>