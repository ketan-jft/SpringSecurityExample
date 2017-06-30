<html>
<head>
    <meta name='layout' content='main'/>
    <title>Reset Password</title>
</head>
<body>
    <center>
    <g:form controller="login" action="setPassword">
        <br><br>
        <g:hiddenField name="sessId" value="${params.sessionId}" />
        <g:passwordField name="newpass" required="required" placeholder="New Password" /><br><br>
        <g:passwordField name="conpass" required="required" placeholder="Confirm Password" /><br><br>
        <g:submitButton name="submit" value="Reset" />
    </g:form>
    </center>
</body>
</html>