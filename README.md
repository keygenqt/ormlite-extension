
Extensions OrmLite for Kotlin
===================

Automatic work with foreign models and automatic generation extensions for easy work with https://ormlite.com/. 

Just **super** KISS work with ormlite)

#### OrmliteBase:
* Connection (init, close)
* Auto gen ComponentScanDb
* Create table if not exist
* Clear all tables in db

#### Extensions:
* One-to-one relationships (Model)
* One-to-many relationships (Collection\<Model>) 
* Clear tables with relationships
* Create and update with relationships
* Model get JSONObject with relationships
* Other extensions functions

#### For connect:

```groovy
// Gradle Groovy DSL
repositories {
    maven {
        url "https://artifactory.keygenqt.com/artifactory/open-source"
    }
}
dependencies {
    kapt 'com.keygenqt.artifactory:gen-ormlite-android:1.0.1'
}
```

```kotlin
// Gradle Kotlin DSL
repositories {
    maven("https://artifactory.keygenqt.com/artifactory/open-source")
}
dependencies {
    kapt("com.keygenqt.artifactory:gen-ormlite-android:1.0.1")
}
```

#### Methods extensions:

```kotlin
// model
Model().delete()
Model().createOrUpdate() // Model
Model().getJSONObject() // JSONObject

// find
Model.find("key", "value") // model
Model.findOneAND(hashMapOf("key" to "value")) // model
Model.findOneOR(hashMapOf("key" to "value")) // model
Model.findAll(hashMapOf("key" to "value")) // list
Model.findAND(hashMapOf("key" to "value")) // list
Model.findOR(hashMapOf("key" to "value")) // list

// Companion
Model.dao() // Dao<Model, String>
Model.clearTable()
Model.clearTableWithForeign()
```

#### Example:

```kotlin
class MainActivity : AppCompatActivity() {

    private lateinit var db: MyOrmliteBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = MyOrmliteBase(this, "name.db", 1)
        setContent {
            MyApplicationTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Example()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }
}

@Composable
fun Example() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        // clear all tables in db
        OrmliteBase.clearDb()

        // clear table only model
        ModelUser.clearTable()

        // clear table model with foreign models
        ModelUser.clearTableWithForeign()

        val id = UUID.randomUUID().toString()

        // example create model with foreign
        ModelUser(
            id,
            "First name",
            "Last name",
            ModelUserAddress("Volgodonsk", "Russia"),
            arrayListOf(
                ModelUserParent("First name mather", "Last name mather"),
                ModelUserParent("First name father", "Last name father"),
            )
        ).createOrUpdate()

        // find model
        ModelUser.find("id", id)?.let {
            Text("It's exist - $id")
            Log.d("TAG", it.getJSONObject().toString())
        } ?: run {
            Text("It's not exist - $id")
        }

        // find model with AND
        ModelUser.findOneAND(hashMapOf("fname" to "First name", "lname" to "Last name"))?.let {
            Text("Find model 'findOneAND'")
            Log.d("TAG", it.getJSONObject().toString())
        }

        // find models list with AND
        ModelUser.findAND(hashMapOf("fname" to "First name", "lname" to "Last name")).firstOrNull()
            ?.let {
                Text("Find model 'findAND'")
                Log.d("TAG", it.getJSONObject().toString())
            }

        // find model with OR
        ModelUser.findOneOR(hashMapOf("fname" to "First name", "lname" to "Last name"))?.let {
            Text("Find model 'findOneOR'")
            Log.d("TAG", it.getJSONObject().toString())
        }

        // find models list with OR
        ModelUser.findOR(hashMapOf("fname" to "First name", "lname" to "Last name")).firstOrNull()?.let {
            Text("Find model 'findOR'")
            Log.d("TAG", it.getJSONObject().toString())
        }

        // find models list
        ModelUser.findAll().firstOrNull()?.let {
            Text("Find model 'findAll'")
            Log.d("TAG", it.getJSONObject().toString())
        }

        // update model
        ModelUser.find("id", id)?.let {
            it.fname = "First name UPDATE"
            it.address.city = "City UPDATE"
            it.parents?.firstOrNull()?.let { p ->
                p.fname = "First name mather UPDATE"
            }
            it.createOrUpdate()
        }

        // show update
        ModelUser.find("id", id)?.let {
            Text("Find after Update model")
            Log.d("TAG", it.getJSONObject().toString())
        }
    }
}
```
