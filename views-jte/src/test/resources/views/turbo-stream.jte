@param Boolean loggedIn
@param String username
<div>
    @if (loggedIn != null && loggedIn)
    <h1>username: <span>${username}</span></h1>
@else
    <h1>You are not logged in</h1>
@endif
</div>
