# Cyber Security Base - Course Project I

This is a Spring-based sign-up management webapp based on the given course example, laden with security vulnerabilities. Some features have been added to the basic example, in order to illustrate better the impact of poor security. The extra functionlities consist in allowing the person who has signed up to remove his/her own enrollment, and to allow the site administrator (admin/admin) to see the entire list and remove anybody from such list. Then, I shot the application full of holes.
The application can be downloaded from github: https://github.com/lpagani/cybersecuritybase-project
Clone the project, open it in netbeans / idea and enjoy.

I have injected the following six vulnerabilities from the OWASP Top 10 vulnerability list: A2:2017 Broken Authentication, A3:2017 Sensitive Data Exposure, A5:2017 Broken Access Control, A6:2017 Security Misconfiguration, A7:2017 Cross-Site Scripting and last but not least A10:2017 Insufficient Logging and Monitoring as this webapp logs absolutely nothing. 
As a bonus, this webapp is vulnerable also to Cross Site Request Forgery.
In the following sections I will describe these vulnerabilities in more detail.

## Vulnerability 1. A2:2017 Broken Authentication
The only authentication requirements in this webapp affect the management pages "/admin" and "/manage". It uses basic HTTP authentication. This means that even after closing the page, the session will stay valid until the browser process is terminated. The password ("admin") is hardcoded and in plaintext.
### How to reproduce:
1. Open http://localhost:8080/admin or click the link in the home page. 
2. Log in with the credentials admin/admin. Close the tab, reopen it. 
### How to fix:
Use a more robust authentication scheme. Use hashed passwords with salt, i.e. by using BCryptPasswordEncoder in the SecurityConfiguration.

## Vulnerability 2. A3:2017 Sensitive Data Exposure
There is no server-side checking of the id parameter which identifies the sign up information, therefore anybody can view any person's information.
In addition plaintext passwords are visible when inspecting the page source code.
### How to reproduce:
1. Go to "http://localhost:8080/manage?id=X" to view the information of user with id=X (X is an integer).
2. Right click and inspect the page to reveal the plaintext passwords.
### How to fix:
Avoid using (and trusting) the id parameter, instead use the session information to determine the user access rights.
Hash passwords, and do not include them in the pages.

## Vulnerability 3. A5:2017 Broken Access Control
As seem in the previews paragraph, the faulty handling and trust put in the id parameter lead to security holes. In this case, anybody logged in can remove anybody's registration by changing the value of the id parameter.
### How to reproduce:
1. Register at least two users.
2. Go to "http://localhost:8080/manage", login to view your information.
3. Check out the Remove link ("http://localhost:8080/remove?id=X"). 
4. Copy the address to the URL bar and replace the id value to delete the registration of another user.
### How to fix:
Again, avoid using (and trusting) the id parameter, instead use the session information to determine the user access rights.

In addition, page "/admin" which is password protected, is accessible by any of the accounts created while signing up.
This shall be fixed by enforcing group based authorization policy.

## Vulnerability 4. A6:2017 Security Misconfiguration
Only HTTP is used, passwords are transmitted and stored in plaintext. Session tokens are 1-byte long.
### How to fix:
Enforce HTTPS to avoid transmitting plaintext password. Store only hashed passwords with salt. Use longer session tokens (CyberSecurityBaseProjectApplication -> customize)

## Vulnerability 5. A7:2017 Cross Site Scripting
The sign up details are not escaped in http://localhost:8080/manage and http://localhost:8080/manage, therefore vulnerable to cross site scripting.

### Identification:
1. Go to http://locahost:8080/form
1. Sign up entering <SCRIPT>alert(document.cookie);</SCRIPT> in to the address field.
2. Click manage

### How to fix:
Change the html templates admin.html and manage.html. 
Replace all instances of “th:utext” to “th:text” to enforce escaping of the user data.

# Vulnerability 6. A10 Insufficient Logging and Monitoring
The application does not produce logs of what happens. It is therefore very difficult to establish that malicious activity has occurred.
### Identification
1. Go to "localhost:8080/form" and add some signups with random names/addresses
2. Go to "localhost:8080/admin" (you can log in with admin/admin)
3. Click remove all.
4. Such action would require logging, as it has permament effect on the data. However, it isn't logged. 

### How to fix:
Significant log messages should be added throughout the application code.

# Bonus Vulnerability. Cross-Site Request Forgery
CSRF tokens are disabled, therefore all forms are vulnerable to CSRF attacks.
 
### Identification
1. Go to "localhost:8080/form" and add some signups with random names/addresses
2. Go to "localhost:8080/admin" (you can log in with admin/admin)
3. Open CSRFTest.html with your browser, which contains the following malicious image: 
<img src="http://localhost:8080/removeAll"/>
5. Go to localhost:8080/admin. All the signups have been removed.

### How to fix:
Enable CSRF protection by removing the line http.csrf().disable() in SecurityConfiguration. 
Spring Boot will automatically enable CSRF tokens for all forms.