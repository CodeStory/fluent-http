/**
 * Copyright (C) 2013 all@code-story.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package net.codestory.http;

import net.codestory.http.payload.Payload;
import net.codestory.http.testhelpers.AbstractWebServerTest;
import org.junit.Test;
import org.reactivecouchbase.json.Format;
import org.reactivecouchbase.json.JsResult;
import org.reactivecouchbase.json.JsValue;
import org.reactivecouchbase.json.Json;

import static org.reactivecouchbase.json.JsResult.combine;
import static org.reactivecouchbase.json.Syntax.$;

public class JsonTest extends AbstractWebServerTest {
    @Test
    public void json_serialization() {

        JsValue personJsValue = Json.obj(
                $("name", "John"),
                $("surname", "Doe"),
                $("age", 42),
                $("address", Json.obj(
                        $("number", "221b"),
                        $("street", "Baker Street"),
                        $("city", "London")
                ))
        );

        String expectedPerson = Json.stringify(personJsValue);

        Person personObj = new Person("John", "Doe", 42,
                new Address("221b", "Baker Street", "London"));

        server.configure(routes -> routes.
                get("/person.json", personJsValue).
                get("/person.obj", Json.toJson(personObj, Person.FORMAT)));

        get("/person.json").produces("application/json", expectedPerson);
        get("/person.obj").produces("application/json", expectedPerson);
    }

    @Test
    public void json_post() {

        JsValue personJsValue = Json.obj(
                $("name", "John"),
                $("surname", "Doe"),
                $("age", 42),
                $("address", Json.obj(
                        $("number", "221b"),
                        $("street", "Baker Street"),
                        $("city", "London")
                ))
        );

        JsValue badPersonJsValue = Json.obj(
                $("name", "John"),
                $("surname", "Doe"),
                $("age", 42),
                $("adresse", Json.obj(
                        $("number", "221b"),
                        $("street", "Baker Street"),
                        $("city", "London")
                ))
        );

        String expectedPerson = Json.stringify(personJsValue);
        String badPerson = Json.stringify(badPersonJsValue);
        String otherPerson = Json.stringify(Json.obj(
            $("name", "Billy"),
            $("surname", "Bob"),
            $("age", 24),
            $("address", Json.obj(
                $("number", "221b"),
                $("street", "Baker Street"),
                $("city", "Paris")
            ))
        ));

        server.configure(routes -> routes.
                post("/persons/format/surname", context -> context.contentFrom(Person.FORMAT)
                        .getOpt().map(p -> new Payload(p.surname).withCode(200)).getOrElse(new Payload("Body is malformed").withCode(400))).
                post("/persons/format/name", context -> context.contentFrom(Person.FORMAT)
                        .getOpt().map(p -> new Payload(p.name).withCode(200)).getOrElse(new Payload("Body is malformed").withCode(400))).
                post("/persons/format/city", context -> context.contentFrom(Person.FORMAT)
                        .getOpt().map(p -> new Payload(p.address.city).withCode(200)).getOrElse(new Payload("Body is malformed").withCode(400))).
                post("/persons/format/age", context -> context.contentFrom(Person.FORMAT)
                        .getOpt().map(p -> new Payload(p.age).withCode(200)).getOrElse(new Payload("Body is malformed").withCode(400))).

                post("/persons/surname", context -> context.contentAsJson().field("surname").as(String.class)).
                post("/persons/name", context -> context.contentAsJson().field("name").as(String.class)).
                post("/persons/age", context -> context.contentAs(JsValue.class).field("age").as(Integer.class)).
                post("/persons/city", context -> context.contentAs(JsValue.class).field("address").field("city").as(String.class)).

                post("/persons/validate", context -> context.contentFrom(Person.FORMAT).isSuccess())
        );

        post("/persons/surname", expectedPerson).produces("Doe");
        post("/persons/name", expectedPerson).produces("John");
        post("/persons/city", expectedPerson).produces("London");
        post("/persons/age", expectedPerson).produces("42");

        post("/persons/validate", expectedPerson).produces("true");
        post("/persons/validate", badPerson).produces("false");

        post("/persons/format/surname", expectedPerson).produces("Doe");
        post("/persons/format/name", expectedPerson).produces("John");
        post("/persons/format/city", expectedPerson).produces("London");
        post("/persons/format/age", expectedPerson).produces("42");

        post("/persons/format/surname", otherPerson).produces("Bob");
        post("/persons/format/name", otherPerson).produces("Billy");
        post("/persons/format/city", otherPerson).produces("Paris");
        post("/persons/format/age", otherPerson).produces("24");

        post("/persons/format/surname", badPerson).produces(400);
        post("/persons/format/name", badPerson).produces(400);
        post("/persons/format/city", badPerson).produces(400);
        post("/persons/format/age", badPerson).produces(400);

    }

    public static class Address {
        public final String number;
        public final String street;
        public final String city;
        public Address(String number, String street, String city) {
            this.number = number;
            this.street = street;
            this.city = city;
        }

        @Override
        public String toString() {
            return "Address{" +
                    "number='" + number + '\'' +
                    ", street='" + street + '\'' +
                    ", city='" + city + '\'' +
                    '}';
        }

        public static final Format<Address> FORMAT =  new Format<Address>() {
            @Override
            public JsResult<Address> read(JsValue value) {
                return combine(
                        value.field("number").read(String.class),
                        value.field("street").read(String.class),
                        value.field("city").read(String.class)
                ).map(input -> new Address(input._1, input._2, input._3));
            }
            @Override
            public JsValue write(Address value) {
                return Json.obj(
                        $("number", value.number),
                        $("street", value.street),
                        $("city", value.city)
                );
            }
        };
    }

    public static class Person {
        public final String name;
        public final String surname;
        public final Integer age;
        public final Address address;
        public Person(String name, String surname, Integer age, Address address) {
            this.name = name;
            this.surname = surname;
            this.age = age;
            this.address = address;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", surname='" + surname + '\'' +
                    ", age=" + age +
                    ", address=" + address +
                    '}';
        }

        public static final Format<Person> FORMAT = new Format<Person>() {
            @Override
            public JsResult<Person> read(JsValue value) {
                return combine(
                        value.field("name").read(String.class),
                        value.field("surname").read(String.class),
                        value.field("age").read(Integer.class),
                        value.field("address").read(Address.FORMAT)
                ).map(input -> new Person(input._1, input._2, input._3, input._4));
            }
            @Override
            public JsValue write(Person value) {
                return Json.obj(
                        $("name", value.name),
                        $("surname", value.surname),
                        $("age", value.age),
                        $("address", Address.FORMAT.write(value.address))
                );
            }
        };
    }
}