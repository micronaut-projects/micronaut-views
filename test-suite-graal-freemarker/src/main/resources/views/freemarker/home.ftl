<!DOCTYPE html>
<html>
<head>
    <title>Title - Freemarker</title>
    <meta charset="utf-8">
</head>
<body>
<#if loggedIn??>
    <h1>username: <span>${username}</span></h1>
<#else>
    <h1>You are not logged in</h1>
</#if>
</body>
</html>
