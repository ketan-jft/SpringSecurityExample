<html>
<head>
    <meta name='layout' content='main'/>
    <title>Reset Password</title>
</head>
<body>
    <center>
    <g:form controller="login" action="resetPassword">
        <br><br>
        <g:textField name="newpass" required="required" placeholder="New Password" /><br><br>
        <g:textField name="conpass" required="required" placeholder="Confirm Password" /><br><br>
        <g:submitButton name="submit" value="Reset" />
    </g:form>
    </center>
</body>
</html>