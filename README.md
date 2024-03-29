# Maja Web Framework

Maja is a modular Java 8 web framework based on [Spark](http://sparkjava.com).

Maja can be extended with these modules:

* maja-mongo: persistence with MongoDB
* maja-nitrite: persistence with [Nitrite](https://github.com/nitrite/nitrite-java) object database
* maja-auth: authorization, contains maja-web
* maja-auth-mongo: authorization with MongoDB support, contains maja-auth and maja-mongo
* maja-redis: caching with Redis

## Documentation
see [Wiki](https://github.com/SoltauFintel/maja/wiki/Maja-framework_de)

see Maja example web app [tankstellen](https://github.com/SoltauFintel/tankstellen)

## How to use
Example for getting maja-auth-mongo with Gradle:

    // in dependencies:
    implementation 'com.github.soltaufintel.maja:maja-auth-mongo:1.1.0'
	
    // in repositories:
    maven { url 'https://jitpack.io' }
