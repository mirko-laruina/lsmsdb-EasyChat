import React from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom'
import LoginForm from './LoginForm.js'
import ChatScreen from './ChatScreen.js'

function App() {
  return (
    <div className="container">
      <Router>
        <Switch>
          <Route path='/' exact component={LoginForm} />
          <Route path='/chat' component={ChatScreen} />
        </Switch>
      </Router>
    </div>
  );
}

export default App;
