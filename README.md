[![Build Status](https://travis-ci.org/mirko-laruina/lsmsdb-EasyChat.svg?branch=master)](https://travis-ci.org/mirko-laruina/lsmsdb-EasyChat)
# Large Scale and Multi Structured Databases - EasyChat
## Development phase
### Spring
The source file is compiled and the Spring server started using Maven

	mvn spring-boot:run
Server listens on `localhost:8080` .

### React
React webapp can be started from `EasyChat/src/main/app` with command

	npm start
Listens on `localhost:3000`.

### Database
Database login information can be configured modifying `server.config` in the project root directory.
Dummy data (with inconsistency between chat members) can be loaded from `database.sql`. Schema-less version can be found in `database_schema_only.sql`.
NOTE: the db has to be created and selected before running the scripts

### Git commands
Before pushing a new commit, it is better to pull from origin

	git pull origin master
	git add file1 file2 etc
	git commit -m "msg"
	git push origin master
## Production
In development phase Spring uses a different server than the webapp, which forces to Cross Origin Resource Sharing (CORS), normally blocked by the browsers.
In order to allow it we had to set the proxy setting in `package.json` and use the `@CrossOrigin` directive in Java/Spring.

In production the problem can be solved running the Spring server which automatically serves all the static files in `/static/`, `/public/` and `/resources/` folder.
Maven has been set-up to automatically compile the React App into static files and moving them in the appropriate directory.
A Jar file can be obtained running

	mvn package
Listens on `localhost:8080`

## License
EasyChat
Copyright (C) 2020  F. Fregosi, M. Laruina, R. Mancini, G. Petrelli

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
