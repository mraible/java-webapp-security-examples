<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
</head>
<body>
<p>
    Please enter your username and password to login.
</p>

<form method="post" id="loginForm" action="/j_security_check">
    <h2>Sign In</h2>

    <c:if test="${param.error == 'true'}">
        <p style="color: red">Login Failed. Please try again.</p>
    </c:if>

    <input type="text" name="j_username" id="j_username" placeholder="Username" required autofocus>
    <input type="password" name="j_password" id="j_password" placeholder="Password" required>

    <input type="submit" name="login" id="login" value="Login">
</form>

<c:if test="${param.ajax}">
    <script src="https://code.jquery.com/jquery-2.1.4.min.js"></script>
    <script type="text/javascript">
        var loginFailed = function (data, status) {
            $(".error").remove();
            $('#j_username').before('<p style="color: red" class="error">Login failed, please try again.</p>');
        };
        $("#login").on('click', function (e) {
            e.preventDefault();
            $.ajax({
                url: "/api/login",
                type: "POST",
                beforeSend: function (xhr) {
                    xhr.withCredentials = true;
                },
                data: $("#loginForm").serialize(),
                success: function (data, status) {
                    if (data.loggedIn) {
                        // success
                        location.href = '/api/health';
                    } else {
                        loginFailed(data);
                    }
                },
                error: loginFailed
            });
        });

    </script>
</c:if>
</body>
</html>