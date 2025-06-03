document.addEventListener('DOMContentLoaded', () => {
    // Get the modal
    const modal = document.getElementById("taskModal");

    // Get the button that opens the modal
    const btn = document.getElementById("openTaskModalBtn");

    // Get the <span> element that closes the modal
    const span = document.getElementsByClassName("close-button")[0];

    // When the user clicks the button, open the modal
    btn.onclick = function() {
        modal.style.display = "flex"; // Change to flex to use flexbox centering
    }

    // When the user clicks on <span> (x), close the modal
    span.onclick = function() {
        modal.style.display = "none";
    }

    // When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }

    const taskForm = document.getElementById("taskForm");
    taskForm.addEventListener('submit', function(event) {
        event.preventDefault(); // Prevent default form submission

        const formData = new FormData(taskForm);
        const taskData = {};
        formData.forEach((value, key) => {
            if (key === 'complete') {
                taskData[key] = true;
            } else {
                taskData[key] = value;
            }
        });
        if (!formData.has('complete')) {
            taskData['complete'] = false;
        }

        console.log('Task data to send:', taskData);

        fetch('/api/tasks', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(taskData),
        })
        .then(response => {
            // First, check if the response was successful (status code 2xx)
            if (!response.ok) {
                // If not successful, try to read the error message from the server
                // and throw an error to be caught by the .catch() block
                return response.json().then(errorData => {
                    throw new Error(errorData.message || 'Server error occurred');
                }).catch(() => { // Catch if parsing JSON fails (e.g., server returned plain text error)
                    throw new Error(`HTTP error! status: ${response.status}`);
                });
            }
            // If successful, parse the JSON response
            return response.json();
        })
        .then(data => {
            // This code runs ONLY if the fetch request was successful and returned valid JSON
            console.log('Task created successfully:', data);
            alert('Task "' + data.title + '" added!');

            // *** MOVE THESE LINES HERE ***
            modal.style.display = "none"; // Close modal on success
            taskForm.reset(); // Clear the form on success
            // Optionally, you might want to refresh a list of tasks displayed on your page here
        })
        .catch(error => {
            // This code runs if there was a network error or an error thrown in the .then() block
            console.error('Error creating task:', error);
            alert('Failed to create task: ' + error.message);
            // DO NOT close modal or reset form on error, so user can fix inputs
        });
    });
});