/**
 * Main JavaScript file for the ticket reservation system
 */

// Wait for the DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {
    // Initialize components based on the current page
    initializePage();
    
    // Add event listeners for interactive elements
    addEventListeners();
});

/**
 * Initialize components based on the current page
 */
function initializePage() {
    // Check if we're on the seat selection page
    if (document.getElementById('seat-chart')) {
        initializeSeatSelection();
    }
    
    // Check if we're on the payment page
    if (document.getElementById('payment-form')) {
        initializeStripePayment();
    }
    
    // Initialize tooltips and popovers if Bootstrap is present
    if (typeof bootstrap !== 'undefined') {
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
        
        var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
        popoverTriggerList.map(function (popoverTriggerEl) {
            return new bootstrap.Popover(popoverTriggerEl);
        });
    }
}

/**
 * Add event listeners for interactive elements
 */
function addEventListeners() {
    // Add fade-out effect for alert messages
    document.querySelectorAll('.alert').forEach(function(alert) {
        setTimeout(function() {
            if (alert) {
                var bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }
        }, 5000); // Auto-close after 5 seconds
    });
    
    // Add confirmation for delete actions
    document.querySelectorAll('.confirm-delete').forEach(function(btn) {
        btn.addEventListener('click', function(e) {
            if (!confirm('Êtes-vous sûr de vouloir supprimer cet élément ?')) {
                e.preventDefault();
            }
        });
    });
}

/**
 * Initialize seat selection functionality
 */
function initializeSeatSelection() {
    const seatChart = document.getElementById('seat-chart');
    const selectedSeatInput = document.getElementById('selected-seat');
    const priceDisplay = document.getElementById('price-display');
    const totalPriceInput = document.getElementById('total-price');
    
    let selectedSeat = null;
    let basePrice = parseFloat(seatChart.dataset.basePrice || 0);
    
    // Add click event for all available seats
    document.querySelectorAll('.seat:not(.reserved)').forEach(function(seat) {
        seat.addEventListener('click', function() {
            // Remove selected class from previously selected seat
            if (selectedSeat) {
                selectedSeat.classList.remove('selected');
            }
            
            // Select the new seat
            seat.classList.add('selected');
            selectedSeat = seat;
            
            // Update hidden input value
            selectedSeatInput.value = seat.dataset.seatId;
            
            // Calculate price based on seat category
            let priceMultiplier = 1;
            if (seat.classList.contains('seat-premium')) {
                priceMultiplier = 1.5;
            } else if (seat.classList.contains('seat-vip')) {
                priceMultiplier = 2;
            }
            
            // Update price display
            const totalPrice = (basePrice * priceMultiplier).toFixed(2);
            priceDisplay.textContent = totalPrice + ' DH';
            totalPriceInput.value = totalPrice;
        });
    });
}

/**
 * Initialize Stripe payment functionality
 */
function initializeStripePayment() {
    // Check if Stripe is loaded and payment form exists
    if (typeof Stripe === 'undefined' || !document.getElementById('payment-form')) {
        return;
    }
    
    const stripePublicKey = document.getElementById('stripe-public-key').value;
    const clientSecret = document.getElementById('client-secret').value;
    
    const stripe = Stripe(stripePublicKey);
    const elements = stripe.elements();
    
    // Create card element
    const card = elements.create('card');
    card.mount('#card-element');
    
    // Handle validation errors
    card.addEventListener('change', function(event) {
        const displayError = document.getElementById('card-errors');
        if (event.error) {
            displayError.textContent = event.error.message;
        } else {
            displayError.textContent = '';
        }
    });
    
    // Handle form submission
    const form = document.getElementById('payment-form');
    form.addEventListener('submit', function(event) {
        event.preventDefault();
        
        // Disable form submission while processing
        document.getElementById('submit-button').disabled = true;
        document.getElementById('loading-indicator').classList.remove('d-none');
        
        // Confirm payment
        stripe.confirmCardPayment(clientSecret, {
            payment_method: {
                card: card
            }
        }).then(function(result) {
            if (result.error) {
                // Show error message
                document.getElementById('card-errors').textContent = result.error.message;
                
                // Re-enable form submission
                document.getElementById('submit-button').disabled = false;
                document.getElementById('loading-indicator').classList.add('d-none');
            } else {
                // Payment succeeded, redirect to success page
                window.location.href = form.dataset.successUrl + '?payment_intent=' + result.paymentIntent.id;
            }
        });
    });
}
