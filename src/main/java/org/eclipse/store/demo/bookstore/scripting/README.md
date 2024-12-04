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

A more complex one, which adds a random purchase:

```
#pragma jexl.import org.eclipse.store.demo.bookstore.data

// select random shop
let shops = data.shops().all();
let shop = shops[faker.random().nextInt(shops.size())];

// select random employee
let employees = shop.employeesList();
let employee = employees[faker.random().nextInt(employees.size())];

// select random customer
let customers = data.customers().all();
let customer = customers[faker.random().nextInt(customers.size())];

// select random book
let books = data.books().all();
let book = books[faker.random().nextInt(books.size())];

let amount = 1;
let item = new PurchaseItem(book, amount);

let timestamp = demo.now();
let purchase = new Purchase(shop, employee, customer, timestamp, [item,...]);

data.purchases().add(purchase);

return purchase;
```

For more information check out the [JEXL syntax reference](https://commons.apache.org/proper/commons-jexl/reference/syntax.html).

