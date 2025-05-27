"use client"
import {CheckoutProvider} from '@stripe/react-stripe-js';
import {loadStripe} from '@stripe/stripe-js';
import CheckoutForm from './checkout';

// Make sure to call `loadStripe` outside of a component’s render to avoid
// recreating the `Stripe` object on every render.
const stripePromise = loadStripe('pk_test_51RK0vPRs3l2T7fmcHfTEXhsV94OFpO8s61CoqhczrFFnHKkVV3vyJiTsb1VExB9N2xlUEnr2bGWksc8Hei37yWev00BqDd1nnl');

const fetchClientSecret = () => {
  return fetch('https://inso-ii.onrender.com/api/create-payment-intent', { method: 'POST', credentials: 'include', headers: { 'Content-Type': 'application/json' }})
    .then((response) => response.json())
    .then((json) => json.clientSecret)
};

export default function App() {
  return (
    <CheckoutProvider stripe={stripePromise} options={{fetchClientSecret}}>
      <CheckoutForm />
    </CheckoutProvider>
  );
}
