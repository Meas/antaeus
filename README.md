## Dear diary

It’s been a while since I wrote to you. How has stuff been in Diary Land? Hope your mom is doing well.

Me? Oh so much stuff has happened since that last time in college, when I was pretending to take notes, but was actually writing to you.

Recently I applied for this great job in Copenhagen. Everything seems awesome so far and I believe I’ve completed the challenge I received for the job!

The challenge? Oh we’ve got the assignment to implement a billing service to handle the monthly payments of pending invoices.

## Actual thought process

At first the task seemed very easy, so I felt as if I didn’t quite understand what I was supposed to do, so I at least tried to get the server fired up in order to see what is happening.

This is the place where I encountered my first obstacle. Firing the Docker constantly resulted in a “File does not exist” type of error. Having no prior experience with Docker, I thought I messed something up during the installation process. At first I tried running it in my host OS - which is Windows (yeah, yeah Windows sucks, I agree, but I have my reasons). After sorting its line ending crashes (Windows!), it still didn’t want to work. 

So I gave up trying to sort it out in Windows environment and went to my trusty VM (Ubuntu 18.04). Soon I found out that I am getting the same error again. Okay, time to do some reading! After reading up a bit about Docker in general, and then reading the included Dockerfile, I’ve tried to execute the commands directly inside the VM, without using Docker.
Hallelujah - I’ve finally got the server running. I’ve tried hitting some API routes and everything seemed fine. After initially building the project from my VM, even running from Docker was working (probably because it didn’t have sufficient rights to create the necessary folder, but now that it was created from VM it was working).

After I got the server running, I tried playing with the routes, trying out how to receive query parameters and POST data in the API since I’ve never worked with Kotlin before (and neither Java for that matter).

Having spent too much time basically preparing to do the challenge, now it was finally time to actually do it. I read a bit more on what’s actually happening in the project and I noticed the standard OOP pattern. Okay finally something I understand!

My idea of how to solve this problem was this: since there are no timestamps in the invoices, I would assume that all the “PENDING” invoices are for the current month, and they should be paid on the first of the following month (between 00:00 and 01:00). Also, since there were multiple currencies in the database, I would assume that the invoices should be paid on the first of month, between 00:00 and 01:00 **in their respective time zones**.

**NOTE**: The reason I’m repeatedly saying “between 00:00 and 01:00” and not “at exactly 00:00” will be explained in the “potential improvements” section.

Everything was going smoothly until I wanted to abstract time zones into the Timezones model, where the key would be the currency, and the value would be the respective time zone of that currency. Boy, was that a hassle. It took me way longer than I would like to admit, but after a lot of googling I managed to do it. Also it occurred to me that I was maybe forcing a pattern I am accustomed to, to a technology I don’t really understand yet. There are probably smarter ways to do something like this in Kotlin.

I proceeded to work on my task, and after some fighting with the syntax and finding the right DateTime class for my needs (where I could easily get the date, time and change the time zone), I was finally done!

The completed logic works as following: 

- When the server starts SchedulerService class runs the first check whether the invoices should be paid and creates an interval to check again every hour.
- There is an API route to do this manually, mostly used it for testing, but could also be used for manually charging eligible invoices
- Added multiple invoice statuses for different responses we could receive from the fake external API
- The check for invoices is triggered every hour because some of the error statuses could be resolved after an hour has passed and should be retried. To explain it further:
    - Invoices with the status “PENDING” should only be paid on the first of month between 00:00 and 01:00 in their respective time zones
    - It makes sense to retry charging invoices of certain type of error status: 
        - ERROR_NETWORK_EXCEPTION (network error might be fixed in an hour)
        - ERROR_NOT_ENOUGH_FUNDS (users might add funds to their account so they can pay for invoice)
        - ERROR_FATAL (we don’t know why this error happened, we should check the logs, fix it and it can be retried)
        - ERROR_SOLVED (in case the error was fixed somewhere else in our system, we can flag the invoice with this status and retry charging)

## Potential improvements

My implementation of SchedulerService is probably abysmal.

The idea is that there should be a universal service for all the tasks that are supposed to be triggered in a certain interval. The whole service’s check function should be run often (maybe once a minute or so) and then check which tasks need to be run (some tasks might run once a week, others once an hour etc.), but because of my lack of knowledge in this technology – thread allocation for the check task, thread allocation for tasks that needs to be run inside if time conditions are met and because my approach might be wrong to start with, I implemented it in a very simple way (run every hour, BillingService checks if there are invoices that need to be paid) and there is definitely space for improvement.

The implementation for updating the invoices is also probably bad. Currently, ALL the invoices that are eligible for charging are taken and sent to the external API. Instead, they should be taken in chunks of *x amount*, charge those and then take another chunk of *x amount*. There is probably some thread allocation for this method too, so that’s why I decided to take the simple approach again.

Also, my implementation of the Timezones model, as mentioned earlier, might be forcing patters from different technologies in this one and might’ve been implemented in a smarter way.

## More dear diary

That’s what I’ve been up to in a nutshell dear diary. I’m sorry that it’s been this long since I wrote to you and I’ll try my best to do it more often. You might be getting a bit jealous when other Diaries get their notes and you have such a negligent pen pal.

Love,

Feđa

**P.S.** Hope you had as much fun reading my (overly long) [README.md](https://github.com/Meas/antaeus/blob/master/README.md) as I had doing this challenge!

## Antaeus

Antaeus (/ænˈtiːəs/), in Greek mythology, a giant of Libya, the son of the sea god Poseidon and the Earth goddess Gaia. He compelled all strangers who were passing through the country to wrestle with him. Whenever Antaeus touched the Earth (his mother), his strength was renewed, so that even if thrown to the ground, he was invincible. Heracles, in combat with him, discovered the source of his strength and, lifting him up from Earth, crushed him to death.

Welcome to our challenge.

## The challenge

As most "Software as a Service" (SaaS) companies, Pleo needs to charge a subscription fee every month. Our database contains a few invoices for the different markets in which we operate. Your task is to build the logic that will pay those invoices on the first of the month. While this may seem simple, there is space for some decisions to be taken and you will be expected to justify them.

### Structure
The code given is structured as follows. Feel free however to modify the structure to fit your needs.
```
├── pleo-antaeus-app
|
|       Packages containing the main() application. 
|       This is where all the dependencies are instantiated.
|
├── pleo-antaeus-core
|
|       This is where you will introduce most of your new code.
|       Pay attention to the PaymentProvider and BillingService class.
|
├── pleo-antaeus-data
|
|       Module interfacing with the database. Contains the models, mappings and access layer.
|
├── pleo-antaeus-models
|
|       Definition of models used throughout the application.
|
├── pleo-antaeus-rest
|
|        Entry point for REST API. This is where the routes are defined.
└──
```

## Instructions
Fork this repo with your solution. We want to see your progression through commits (don’t commit the entire solution in 1 step) and don't forget to create a README.md to explain your thought process.

Happy hacking 😁!

## How to run
```
./docker-start.sh
```

## Libraries currently in use
* [Exposed](https://github.com/JetBrains/Exposed) - DSL for type-safe SQL
* [Javalin](https://javalin.io/) - Simple web framework (for REST)
* [kotlin-logging](https://github.com/MicroUtils/kotlin-logging) - Simple logging framework for Kotlin
* [JUnit 5](https://junit.org/junit5/) - Testing framework
* [Mockk](https://mockk.io/) - Mocking library
