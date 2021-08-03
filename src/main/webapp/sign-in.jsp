<%-- Document : sign-in Created on : Jul 29, 2021, 10:59:27 AM Author : Victor Okonkwo --%>

    <%@page contentType="text/html" pageEncoding="UTF-8" %>
        <!DOCTYPE html>
        <html>

        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <meta http-equiv="X-UA-Compatible" content="ie=edge">
            <title>JSP Page</title>
            <link rel="stylesheet" type="text/css" href="./css/style.css">
        </head>

        <body>
            <main class="page-container">
                <section class="page-container__left-side">
                    <img src="./assets/jackson-david-BL_Q4zjduGU-unsplash.jpg" alt="woman in yellow background"
                         title="African Sound" class="page-container__left-side__image" loading="lazy"/>
                    <!-- logo and text--> 
                    <div class="page-container__left-side__element-container">
                        <!-- logo -->
                        <div class="logo-box"> 
                            <div class="logo">
                                <span>&nbsp;</span>
                                <span>&nbsp;</span>
                                <span>&nbsp;</span>
                            </div>
                            <p class="logo-text--white logo-text"> SoundOn</p>
                        </div>
                        
                        <!-- text -->
                        <p class="page-container__left-side__text"> find the right frequency anywhere, anytime.</p>
                        
                        <a class="btn btn--blue" href="#">sign up</a>
                    </div>
                    
                </section>
                <section class="page-container__right-side">
                    <!-- logo -->
                        <div class="logo-box"> 
                            <div class="logo">
                                <span>&nbsp;</span>
                                <span>&nbsp;</span>
                                <span>&nbsp;</span>
                            </div>
                            <p class="logo-text--black logo-text"> SoundOn</p>
                        </div>
                    
                    <p class="right-side__heading"> sign up for an account</p>
                    <form class="form-group" method="POST" action="/sign-in">
                        
                    </form>
                    
                    <a class="btn btn--blue" href="#">register</a>
                </section>
            </main>
        </body>

        </html>