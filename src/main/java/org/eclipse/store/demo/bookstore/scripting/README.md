# Scripting with Apache JEXL

This demo opens a REST endpoint under `http://localhost:8080/script/run` where arbitrary [JEXL](https://commons.apache.org/proper/commons-jexl/) scripts can be executed.

It exposes the root object as a variable called `data`.

Looking at the `Data` root object, it has a method `customers()`, and the `Customers` object has a method `customerCount()`.

Following script will return the customer count:

`data.customers().customerCount()`

Use a tool of your choice to execute the REST call, e.g. cURL:

```
curl -X POST --header "Content-Type: text/plain" -d "data.customers().customerCount()" http://localhost:8080/script/run
```

Not only expressions can be executed but complete scripts. 
This one gets the first `BookSales` object of the 2024 bestseller list.
Then it returns the amount and book title only.

```
let sell = data.purchases().bestSellerList(2024)[0];
return {sell.book().title() : sell.amount()};
```

For more information check out the [JEXL syntax reference](https://commons.apache.org/proper/commons-jexl/reference/syntax.html).

