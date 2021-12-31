# Why Nitrite database?

I needed a simple to use database for my NAS server with ARM processor. I didn't know how to install MongoDB.
So I looked for a similar free database written in Java.
The classes Database and AbstractDAO are similar to maja-mongo. Using [Nitrite](https://github.com/nitrite/nitrite-java) DB
you must distinguish between INSERT and UPDATE.
