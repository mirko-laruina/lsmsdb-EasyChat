import React, {Component} from 'react';
import { Button, Form, Alert } from "react-bootstrap";
import {Link, Switch, Route, BrowserRouter as Router} from 'react-router-dom';
import Cookies from 'universal-cookie';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import './login.css';

const cookies = new Cookies();

class LoginForm extends Component {
  constructor(){
    //Controllare se lo stato serve poi
    super();
    this.state = {
      username: '',
      password: '',
      wrongAuth: 'hidden',
    };

    this.handleSubmit = this.handleSubmit.bind(this)
  }

  handlePw(value){
    this.setState({
      password: value
    });
  }

  handleUser(value){
    this.setState({
      username: value
    });
  }

  handleSubmit(evt){
    evt.preventDefault();
    var self = this;
    var postUrl;
    this.setState({
      wrongAuth: 'hidden',
    }) 
    var path = window.location.href.split('/')[3]
    if(path !== 'register'){
      postUrl = 'http://'+window.location.hostname+':8080/api/v1/auth/login';
    } else {
      postUrl = 'http://'+window.location.hostname+':8080/api/v1/users';
    }

    axios.post(postUrl, {
      username: this.state.username,
      password: this.state.password
    })
    .then(function (response) {
        if(response['data']['success']){
          self.setState({
            wrongAuth: 'hidden',
          })
          cookies.set('sessionId', response['data']['sessionId']);
          cookies.set('username', self.state.username);
          window.location.reload();
        } else {
          self.setState({
            wrongAuth: '',
          })        
        }
    })
    .catch(function (error) {
      console.log(error);
    });
  }

  render() {
    return (
      <div className="Login">
        <Router>
          <Form onSubmit={(evt) => this.handleSubmit(evt, this.props)}>
            <h2>EasyChat</h2>
            <Form.Group controlId="formUsername">
              <Form.Label>Username</Form.Label>
              <Form.Control type="text"placeholder="Enter username"
                            value={this.state.username} onChange={(evt) => this.handleUser(evt.target.value)}/>
            </Form.Group>

            <Form.Group controlId="formPassword">
              <Form.Label>Password</Form.Label>
              <Form.Control type="password" placeholder="Password"
                            value={this.state.password} onChange={(evt) => this.handlePw(evt.target.value)} />
            </Form.Group>

            <Switch>
              <Route path="/register">
                <Alert variant="danger"
                  style={{ marginBottom: '20px'}}
                  className={this.state.wrongAuth ? 'hidden' : ''}>Username already taken</Alert>
                <Link to="/">
                  <Button variant="link">Log in</Button>
                </Link>
                <Button variant="primary" type="submit">
                  Sign on!
                </Button>
              </Route>
              <Route path="/">
                <Alert variant="danger"
                  style={{ marginBottom: '20px'}}
                  className={this.state.wrongAuth ? 'hidden' : ''}>Wrong username or password</Alert>
                <Link to="/register">
                  <Button variant="link">Sign on!</Button>
                </Link>
                <Button variant="primary" type="submit">
                  Log in!
                </Button>
              </Route>
            </Switch>
          </Form>
        </Router>
      </div>
    );
  }
}

export default LoginForm;
