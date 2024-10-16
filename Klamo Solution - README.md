# Michael Klamo's Super Awesome README File
## And sorta okay code too I guess ¯\\\_(ツ)\_\/¯

### FIRST, Heads-up:
* There is (or might be?) a bug which exists in both the default `README` and the provided `producer.jar` test script.
  * Line 4 of the example payload in both of these has an invalid value for y. It seems that 1 character is missing, which causes it to fall below the minimum y of `1,073,741,823`:
    * `1607341271814,0.0586780608,111212767`
    * `1607341261814,0.0231608748,1539565646`
  * My code returns `400 Bad Request` when all events in a request are invalid (and I consider "y being too small" to be invalid).
    * The test code in the jar does not like this, and throws a `ClientRequestException` when it receives the `400`.
    * If you still want to test using the .jar, you must go into `Event.parseEventFromString()` and comment out lines 61 and 62, where the y-minimum check is performed.

### Configuration
* This runs on port `8080` by default.
  * To change this port, go to `/src/main/java/resources/application.properties` and change the value of `server.port`

### Running 
* To build and run in Maven via command-line/terminal:
  * Navigate to `KlamoCodingChallenge` folder. 
  * Run `.\mvnw compile exec:java`
* To build and run in IntelliJ Idea:
  * Load project in IntelliJ Idea.
  * Make sure `KlamoCodingChallengeApplication` is selected in `Run / Debug Configurations`.
  * Select `Run` or `Debug`.

### Testing
* To run unit tests with Maven via command-line/terminal:
  * Navigate to `KlamoCodingChallenge` folder.
  * Run `.\mvnw clean test`
* To run unit test in IntelliJ Idea:
  * Load project in IntelliJ Idea.
  * Right click on project in "Project" pane.
  * Select `Run 'All Tests'` or `Debug 'All Tests'`
* To manually test:
  * I have provided the Postman collection I used for manual/automated-ish testing for this code challenge. You can find this in the `KlamoPostman` folder.
    * If you use Postman, you can import these as a v2.1 collection – whether just to look or to help in testing.
    * Of particular note is `Generate and Send 5 Valid Events, Logging Stats to Console`.
      * This does as it says. You can find the generated (expected) stats in the Postman console (`Ctrl + Alt + C` to open);
      * If you'd like, you can increase this from 5 to any `n` by:
        * 1: Changing the `total` variable in the request's `Scripts` > `Pre-Request`.
        * 2: Manually adding placeholders for these variables in the request's `Body`.
    * I am sure there is a way to fire a follow-up call to the `/stats` endpoint and compare directly against what it returns, but I’m not sure how to do that. This manual string comparison is mainly meant as a sanity check.
* Note:
  * I acknowledge I got a little lax with implementing tests as time went on, particularly for `EventManagerTest` and `MainRestControllerTest`. I won't redefend myself, as I've already got comments and commit messages doing so, so I'll just say I know better and can do better, and these aren't necessarily a great representation of the test I write in production.

### Assumptions:
* Even though only events less than a minute old are used, I assumed older events were not meant to just be discarded.
* Multiple events can have the same `timestamp`, so long as they have different `X` and/or `Y` values.
  * This is because the example payload has 2 events with the same timestamp (`1607341331814`).
  * Events with the exact same `timestamp`, `X`, and `Y` values are considered the same event (not really intentionally, just as a result of how the `Event.compareTo()` got implemented).

### Notes:
* I think this may have been the first time I touched Spring boot (I even initially approached this project from an [HttpServer](https://www.codeproject.com/Tips/1040097/Create-a-Simple-Web-Server-in-Java-HTTP-Server) perspective), so I'm sure I'm overlooking a bunch of best practices and simpler ways to do things.
* My implementation of `postEvent()` returns a `202 Accepted` when all events in the request are valid, as per the instructions, though it feels a little odd as I'm used to this status code being used when submitting asynchronous requests via an API (usually followed by polling to see if some subsequent processing is done).
  * Also note, `postEvent()` returns a `400 Bad Request` when all events are INVALID, and a `207 Multi-Status` when there are 1+ valid and 1+ invalid events.
  * The response body for `postEvent()` is XML, and contains an individual parsing result for each input line with an explanation as to whether it was valid and why.
* I certainly wasn't able to satisfy the bonus requirement.
  * I actually had a hard time figuring out how to handle event storage in general.
  * I kept getting hung up on implementing this as a coding exercise/exam and implementing it like I would if it was a production app.
    * I was tempted to just toss all events older than 1 minute, as they were never being used beyond that, but I also figured a production app might hold onto events for longer, and lazily discarding events would probably be bad form.
    * Reconciling efficiency with the requirement to tolerate concurrency was difficult. I ended up relying on using `Collections.synchronizedSortedSet()` to wrap a `TreeSet`, but I'm not sure whether my usage of it is all that efficient or how it performs.
    * It was also difficult to factor in whether it'd be better to design things around cleaning up old events when inserting new events or when accessing the list... and it just occurred to me that I ended up not cleaning up old events at all. 🤦‍♂️ Still, I suppose that's closer to the instructions of "don't use cleanup threads"?
    * I just kept trying to prematurely optimize before even having a solution.
      * I spent probably way too much time looking up differences between `LinkedHashMap` and `Cache` and `LoadingCache` and `Concurrent...Queue? I think?` and the concept of [Timing Wheels(?)](https://omux.dev/blog/timing-wheels-an-in-depth-exploration/) and `HashedWheelTimer`... I just couldn't find a solution I was really happy with.
  * So I guess my point in general is that my code is probably not all that efficient, but I did spend a good amount of time trying to think about and prioritize efficiency, for what it's worth!
* Note that `postEvent()` is relying on `Event.parseEventFromString(..)` to handle parsing incoming event strings, and this method throws an `IllegalArgumentException` for invalid events, and the message of that exception gets passed directly to the response body.
  * This feels sketchy from a security perspective, but these are just messages I explicitly wrote out and not stack traces, so I felt it wasn't too egregious.
* ` /teapot`
  * ☕🍵

---

### Final Thoughts

I think that's just about everything!

Please let me know if you have any questions, or if you need anything further from me.

Thank you for taking the time to review this, and have a great week!

- Michael Klamo
