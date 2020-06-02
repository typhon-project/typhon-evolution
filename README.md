[![Build Status](http://typhon.clmsuk.com:8080/buildStatus/icon?job=TyphonEvolution)](http://typhon.clmsuk.com:8080/job/TyphonEvolution)

# Typhon evolution tools

This repository provides a set of tools helping you to evolve the polystore schema. The tools are the following : 

- [Evolution CLI](#evolution-cli) : A command line tool enabling you to change the schema deploied in the polystore,
- [Query Evolution Plugin](#query-evolution-plugin) : An eclipse plugin helping you to translate TyphonQL queries for the new schema,
- [Injection Tool](#injection-tool) : Allows you to inject an external database to the polystore schema,
- [Analytics Tool](#analytics-tool) : An interface enabling you to visualize the performance of the polystore.

The instruction for installing and using each of these tools are explicited in the following sections.

## Evolution CLI

The evolution CLI evolve the schema of your polystore accordingly to the changeoperators of the new tml file.

### Installation

First, you need to clone the evolution tool repository with the following command :

```
git clone git@github.com:typhon-project/typhon-evolution.git
```

and move to the folder called "backend".

The first step of the installation is to add jar to your maven repository. You just need to execute the file called `mvn.cmd`located at `src/main/ressources/libs`.

Once the dependencies are successfully installed, you can go back to the root of the `backend` folder and run :

```
mvn install
```


### Usage

The usage could be seen by running `java -jar command`


## Query Evolution Plugin

The Eclipse plugin allows you to translate typhonQL queries to make them works with a new version of the schema enabling the programmer to quickly update their applications to the new polystore.

### Installation



### Usage

The plugin introduce a new file format with the extension .qevo. The structure of the file is the following :

- Schema import : A line specifying the .tml file with the change operators applied to the schema,
- Queries : A set of queries separated by a comma. Those queries will be transformed accordingly to the changeOperators contained in the .tml file.

Here is an example of .qevo file :

```
import /src/schema.xmi ;

from Product p select p,

from User u, Order o select o where u.orders == o, u.id == "324",

from User u select u.orders where u.id == "324",

from User u select u.orders.paidWith,


from Product p select p.review,

from Review r select r where r.product.id == "product_id",

insert
    @pp User { name: "Pablo", reviews: badradio },
    @radio Product {name: "Radio", description: "Wireless", reviews: badradio },
    @badradio Review { contents: "Bad radio",product: radio,user: pp},
    
delete Item p where p.name == "Half Life 3"
```

The path of the .tml file is relative to the eclipse project where the .qevo file is created. 
To transform the queries, the user should right click on the .qevo file and select the transform option

%TODO : add screenshot

A status is assigned to each transformed queries : 

- **Unchanged** : The query is not impacted by the change operators and is unchanged
- **Modified** : The query is impacted by one of the change operators and was modified. The result set returned by the query is unchanged
- **Warning** : The query is impacted by one of the change operators and was modified. However, the result set returned by the query may not have the same shape as before.
- **Broken** : The query is impacted by one of the change operators and could not work anymore on the new schema. 

An explanation of why the query is set as *Warning* or *Broken* is also provided to help the developper to assess the issue.

## Injection Tool


## Analytics Tool