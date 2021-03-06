package com.example.roomwordsample;

public class CodeNotes {
	/*Android Room with a View - Java
1)  Introduction
		The purpose of Architecture Components is to provide guidance on app architecture, with
		libraries for common tasks like lifecycle management and data persistence.
		The Architecture Component libraries are part of Android Jetpack.

		Architecture components help you structure your app in a way that is robust, testable, and maintainable with less boilerplate code.

		What are the recommended Architecture Components?
			Here is a short introduction to the Architecture Components and how they work together.
			Note that this codelab focuses on a subset of the components, namely LiveData, ViewModel and Room.
			Each component is explained more as you use it.
			Entity:
				Annotated class that describes a database table when working with Room.
			SQLite database:
				On device storage. The Room persistence library creates and maintains this database for you.
			DAO:
				Data access object. A mapping of SQL queries to functions. When you use a DAO,
				you call the methods, and Room takes care of the rest.
			Room database:
			    Simplifies database work and serves as an access point to the underlying SQLite database (hides SQLiteOpenHelper). The Room database uses the DAO to issue queries to the SQLite database.
			Repository:
				Used to manage multiple data sources.
			ViewModel:
				Acts as a communication center between the Repository (data) and the UI.
				The UI no longer needs to worry about the origin of the data.
				ViewModel instances survive Activity/Fragment recreation.

LiveData: A data holder class that can be observed. Always holds/caches the latest version of data, and notifies its observers when data has changed. LiveData is lifecycle aware. UI components just observe relevant data and don't stop or resume observation. LiveData automatically manages all of this since it's aware of the relevant lifecycle status changes while observing.
2)  Create the app
		Open Android Studio and create an app as follows:
			*Choose the Empty Activity
			*name the app
3)  Update Gradle files
		You have to add the component libraries to your Gradle files.
		In your build.gradle (Module: app) make the following changes:
			Add the following compileOptions block inside the android block to set target and source
			compatibility to 1.8, which will allow us to use JDK 8 lambdas later on.
				compileOptions {
                    sourceCompatibility = 1.8
                    targetCompatibility = 1.8
                    }
			Add the following code to the end of the dependencies block:
				// Room components
				implementation "androidx.room:room-runtime:$rootProject.roomVersion"
				annotationProcessor "androidx.room:room-compiler:$rootProject.roomVersion"
				androidTestImplementation "androidx.room:room-testing:$rootProject.roomVersion"
				// Lifecycle components
				implementation "androidx.lifecycle:lifecycle-extensions:$rootProject.archLifecycleVersion"
				annotationProcessor "androidx.lifecycle:lifecycle-compiler:$rootProject.archLifecycleVersion"
				// UI
				implementation "com.google.android.material:material:$rootProject.materialVersion"
				// Testing
				androidTestImplementation "androidx.arch.core:core-testing:$rootProject.coreTestingVersion"
			In your build.gradle (Project: RoomWordsSample) file, add the version numbers to the end of the file,
			as given in the code below and then sync the project.

				Get the most current version numbers from the Adding Components to your Project page.

					ext {
                        roomVersion = '2.2.1'
                        archLifecycleVersion = '2.2.0'
                        coreTestingVersion = '2.1.0'
                        materialVersion = '1.0.0'
					}
					
4)Create an Entity
			Architecture components allow you to create tables to hold those values via an Entity:
			Create a new class file called Word.
			This class will describe the Entity (which represents the SQLite table) for your words.
			Each property in the class represents a column in the table. Room will ultimately use
			these properties to both create the table and instantiate objects from rows in the database.
			Here is the code:

				public class Word {
                    private String mWord;
                    public Word(@NonNull String word) {this.mWord = word;}
                    public String getWord(){return this.mWord;}
				}
				
			To make the Word class meaningful to a Room database, you need to annotate it.
			Annotations identify how each part of this class relates to an entry in the database.
			Room uses this information to generate code.

			Update your Word class with annotations as shown in this code:
				@Entity(tableName = "word_table")
				public class Word {
                    @PrimaryKey
                    @NonNull
                    @ColumnInfo(name = "word")
                    private String mWord;
                    public Word(String word) {this.mWord = word;}
                    public String getWord(){return this.mWord;}
				}
				
			When you copy paste code, you may have to import the annotation classes manually.
			You can move the cursor to the code for each error and use the "Project quick fix"
			keyboard shortcut (Alt+Enter on Windows/Linux, Option+Enter on Mac) to import classes quickly.
				import androidx.room.ColumnInfo
				import androidx.room.Entity
				import androidx.room.PrimaryKey
		Note that if you type the annotations yourself (instead of pasting), Android Studio will auto-import.
		Let's see what these annotations do:

			@Entity(tableName = "word_table")
			Each @Entity class represents a SQLite table. Annotate your class declaration to indicate that it's an entity. You can specify the name of the table if you want it to be different from the name of the class. This names the table "word_table".
			@PrimaryKey
			Every entity needs a primary key. To keep things simple, each word acts as its own primary key.
			@NonNull
			Denotes that a parameter, field, or method return value can never be null.
			@ColumnInfo(name = "word")
			Specify the name of the column in the table if you want it to be different from the name of the member variable.
			Every field that's stored in the database needs to be either public or have a "getter" method. This sample provides a getWord() method.
			You can find a complete list of annotations in the Room package summary reference.
		See Defining data using Room entities.
		
		Tip: You can autogenerate unique keys by annotating the primary key as follows:
			@Entity(tableName = "word_table")
			public class Word {
			@PrimaryKey(autoGenerate = true)
			private int id;
			@NonNull
			private String word;
			//..other fields, getters, setters
		}


5) Create the DAO
	(data access object) validates your SQL at compile-time and associates it with a method.
    Room uses the DAO to create a clean API for your code.
    The DAO must be an interface or abstract class.
    By default, all queries must be executed on a separate thread.
	  
    @Dao  identifies it as a DAO class for Room.
    @Insert @Delete and @Update annotations take care of providingSQL
	@Insert(onConflict = OnConflictStrategy.IGNORE) // ignores a new word if already in the list
	void insert(Word word);     //Declares method to insert one word
	
	@Query("DELETE FROM word_table") // @Query requires a SQL query as a string parameter.
	void deleteAll();   //declares a method to delete all the words
	
	@Query("SELECT * FROM word_table ORDER BY word ASC") //Returns a list of words sorted in ascending order.
	List<Word> getAlphabetizedWords(); //A method to get all the words and have it return a List of Words.

6) The LiveData class
	When data changes you usually want to take some action, such as displaying the updated data in the UI.
	This means you have to observe the data so that when it changes, you can react.
	Depending on how the data is stored, this can be tricky. Observing changes to data across
	multiple components of your app can create explicit, rigid dependency paths between the components.
	This makes testing and debugging difficult, among other things.

	LiveData, a lifecycle library class for data observation, solves this problem.
	Use a return value of type LiveData in your method description, and Room generates all necessary
	code to update the LiveData when the database is updated.

	Note: If you use LiveData independently from Room, you have to manage updating the data.
	LiveData has no publicly available methods to update the stored data.
	To update data stored within LiveData, you must use MutableLiveData instead of LiveData.
	The MutableLiveData class has 2 public methods to set the value of a LiveData object,
		*  setValue(T) and postValue(T).
	Usually, MutableLiveData is used within the ViewModel, and then the ViewModel only exposes
	immutable LiveData objects to the observers.

7) Add a ROOM DATABASE
	Room is a database layer on top of an SQLite database.
	Room takes care of mundane tasks that you used to handle with an SQLiteOpenHelper.
	Room uses the DAO to issue queries to its database.
	Room doesn't allow you to issue queries on the main thread, to avoid poor UI performance.
	When Room queries return LiveData, the queries are auto run async on a background thread.
	Room provides compile-time checks of SQLite statements.
	
	Implement the Room database
	Your Room database class must be abstract and extend RoomDatabase.
	Usually, you only need one instance of a Room database for the whole app.
	*Let's walk through the code:
		1 The database class for Room must be abstract and extend RoomDatabase
		2 @Database Room database, annotation parameters to declare the entities that belong in the
		    database and set the version number.
		    Each entity corresponds to a table that will be created in the database.
		    Database migrations are beyond the scope of this codelab, so we set exportSchema to false
		    here to avoid a build warning.
		    In a real app, you should consider setting a directory for Room to use to export the schema
		    so you can check the current schema into your version control system.
		3 The database exposes DAOs through an abstract "getter" method for each @Dao.
		4 We've defined a singleton, WordRoomDatabase, to prevent having multiple instances
			of the database opened at the same time.
		5   getDatabase returns the singleton. It'll create the database the first time it's accessed, using Room's database builder to create a RoomDatabase object in the application context from the WordRoomDatabase class and names it "word_database".
		6   We've created an ExecutorService with a fixed thread pool that you will use to run database operations asynchronously on a background thread.
	 
8) Create the REPOSITORY
	A Repository class abstracts access to multiple data sources.
	The Repository is not part of the Architecture Components libraries, but is a suggested
	best practice for code separation and architecture.
	A Repository class provides a clean API for data access to the rest of the application.
	
	Why use a Repository?
		A Repository manages queries and allows you to use multiple backends.
		In the most common example, the Repository implements the logic for deciding
		whether to fetch data from a network or use results cached in a local database.
        ** Note:to unit test the WordRepository, you have to remove the Application dependency.
        This adds complexity and much more code, and this sample is not about testing.
        See the BasicSample in the android-architecture-components repository at https://github.com/googlesamples
	
	The main takeaways:
		1   The DAO is passed into the repository constructor as opposed to the whole database.
			You only need access to the DAO, since it contains all the read/write methods for the db.
			There's no need to expose the entire database to the repository.
		2   The getAllWords method returns the LiveData list of words from Room;
			we can do this because of how we defined the getAlphabetizedWords method to return LiveData
			in the "The LiveData class" step. Room executes all queries on a separate thread.
			Then observed LiveData will notify the observer on the main thread when the data has changed.
		3   We need to not run the insert on the main thread, so we use the ExecutorService we
			created in the WordRoomDatabase to perform the insert on a background thread.
	
9) Create the ViewModel
	The ViewModel's role is to provide data to the UI and survive configuration changes.
	A ViewModel acts as a communication center between the Repository and the UI.
	You can also use a ViewModel to share data between fragments.
	The ViewModel is part of the lifecycle library.
	
	Why use a ViewModel?
		A ViewModel  app UI data in a lifecycle-conscious way that survives configuration changes.
		Separating your app's UI data from your Activity and Fragment classes lets you better
		follow the single responsibility principle:
		*** Activities and Fragments are responsible for drawing data to the screen
		*** ViewModel holds and processes all the data needed for the UI.
		
		In the ViewModel, use LiveData for changeable data that the UI will use or display.
		Using LiveData has several benefits:
			1   You can put an observer on the data (instead of polling for changes) and only update
				the UI when the data actually changes.
			2   The Repository and the UI are completely separated by the ViewModel.
			3   No database calls from the ViewModel (handled by Repository), code = more testable.
	
	Implement the ViewModel
		1   Create a class called WordViewModel that gets the Application as a parameter and extends AndroidViewModel.
		2   Add a private member variable to hold a reference to the repository.
		3   Add a getAllWords() method to return a cached list of words.
		4   Implement a constructor that creates the WordRepository.
		5   In the constructor, initialize the allWords LiveData using the repository.
		6   Create a wrapper insert() method that calls the Repository's insert() method.
			In this way, the implementation of insert() is encapsulated from the UI.
	
		Warning:
		Don't keep a reference to a context that has a shorter lifecycle than your ViewModel!
		    Examples are: Activity, Fragment, and View
			Keeping a reference can cause a memory leak, e.g. the ViewModel has a reference to a destroyed Activity!
			All these objects can be destroyed by the operative system and recreated when there's
			a configuration change, and this can happen many times during the lifecycle of a ViewModel.
			If you need the application context (which has a lifecycle that lives as long as the application does),
			   use AndroidViewModel, as shown in this codelab.

		Important:
		ViewModels don't survive the app's process being killed in the background when the OS needs more resources.
			For UI data that needs to survive process death due to running out of resources,
			you can use the Saved State module for ViewModels.
			
11) ADD A RECYCLERVIEW
	Display the data in a RecyclerView, nicer than a TextView.
		Note that the mWords variable in the adapter caches the data.
		*
		*
12) Populate the Database
       There is no data in the database. You will add data in two ways:
            Add some data when the database is opened, and add an Activity for adding words.
		To delete all content and repopulate the db when the app starts,
		* create a RoomDatabase.Callback and override onOpen().
		
		Note: If you only want to populate the database the first time the app is launched,
		you can override the onCreate() method within the RoomDatabase.Callback.

        onOpen() uses the previously defined databaseWriteExecutor to execute a lambda on a
            background thread, because you cannot do Room database operations on the UI thread.
            The lambda deletes the contents of the database, then populates it as specified.
        Then, add the callback to the database build sequence right before
           calling .build() on the Room.databaseBuilder()
    
13) Add New Activity
		Add these string resources in values/strings.xml:
			<string name="hint_word">Word...</string>
			<string name="button_save">Save</string>
			<string name="empty_not_saved">Word not saved because it is empty.</string>
		Add this color resource in value/colors.xml: <color name="buttonLabel">#FFFFFF</color>
		Create a new dimension resource file:
			Select File > New > Android Resource File.
			From the Available qualifiers, select Dimension.
			Set the file name: dimens
			Add these dimension resources in values/dimens.xml:
				<dimen name="small_padding">8dp</dimen>
				<dimen name="big_padding">16dp</dimen>
		Create a new empty Android Activity with the Empty Activity template:
			Select File > New > Activity > Empty Activity
			Enter NewWordActivity for the Activity name.
			Verify that the new activity has been added to the Android Manifest!
					<activity android:name=".NewWordActivity"></activity>
		Update the activity_new_word.xml file in the layout folder with the following code:
			<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
                android:orientation="vertical"
                android:layout_width="match_parent"
                android:layout_height="match_parent">
            <EditText
                android:id="@+id/edit_word"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:minHeight="@dimen/min_height"
                android:fontFamily="sans-serif-light"
                android:hint="@string/hint_word"
                android:inputType="textAutoComplete"
                android:layout_margin="@dimen/big_padding"
                android:textSize="18sp" />
            <Button
                android:id="@+id/button_save"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:background="@color/colorPrimary"
                android:text="@string/button_save"
                android:layout_margin="@dimen/big_padding"
                android:textColor="@color/buttonLabel" />
			</LinearLayout>
	Update the code for the activity:
		public class NewWordActivity extends AppCompatActivity {
        public static final String EXTRA_REPLY = "com.example.android.wordlistsql.REPLY";
        private  EditText mEditWordView;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_new_word);
            mEditWordView = findViewById(R.id.edit_word);
            final Button button = findViewById(R.id.button_save);
            button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
               Intent replyIntent = new Intent();
               if (TextUtils.isEmpty(mEditWordView.getText())) {
                   setResult(RESULT_CANCELED, replyIntent);
               } else {
                   String word = mEditWordView.getText().toString();
                   replyIntent.putExtra(EXTRA_REPLY, word);
                   setResult(RESULT_OK, replyIntent);
                     }
                finish();
                 }
             });
         }
		}
14) Connect the Data
		The final step is to connect the UI to the database by saving new words the user enters and
		displaying the current contents of the word database in the RecyclerView. To display the
		current contents of the database, add an observer that observes the LiveData in the ViewModel.
		Whenever the data changes, the onChanged() callback is invoked, which calls the
		adapter's setWords() method to update the adapter's cached data and refresh the displayed list.
	  
	    * In MainActivity, create a member variable for the ViewModel:
				private WordViewModel mWordViewModel;
		Use ViewModelProvider to associate your ViewModel with your Activity.
		When your Activity first starts, the ViewModelProviders will create the ViewModel.
		When the activity is destroyed, for example through a configuration change, the ViewModel persists.
		When the activity is re-created, the ViewModelProviders return the existing ViewModel.
		For more information, see ViewModel.
		
		* In onCreate() below the RecyclerView code block, get a ViewModel from the ViewModelProvider.
				mWordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
		* Also in onCreate(), add an observer for the LiveData returned by getAlphabetizedWords().
		The onChanged() method fires when the observed data changes and the activity is in the foreground.
				mWordViewModel.getAllWords().observe(this, new Observer<List<Word>>() {
                @Override
                    public void onChanged(@Nullable final List<Word> words) {
                     // Update the cached copy of the words in the adapter.
                        adapter.setWords(words);
                     }
					});
		* Define a request code as a member of the MainActivity:
				public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;
		* In MainActivity, add the onActivityResult() code for the NewWordActivity.
				If the activity returns with RESULT_OK, insert the returned word into the database
				by calling the insert() method of the WordViewModel.

				public void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);

                if (requestCode == NEW_WORD_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
                    Word word = new Word(data.getStringExtra(NewWordActivity.EXTRA_REPLY));
                     mWordViewModel.insert(word);
                } else {
                    Toast.makeText(
                             getApplicationContext(),
                            R.string.empty_not_saved,
                            Toast.LENGTH_LONG).show();
                }
			}
		* In MainActivity,start NewWordActivity when the user taps the FAB.
			In the MainActivity onCreate, find the FAB and add an onClickListener with this code:
				FloatingActionButton fab = findViewById(R.id.fab);
				fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewWordActivity.class);
                startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
                }
			});
		* RUN YOUR APP!!!
			When you add a word to the database in NewWordActivity, the UI will automatically update.
			In onCreate() below the RecyclerView code block, get a ViewModel from the ViewModelProvider.
	  
	  */
}
