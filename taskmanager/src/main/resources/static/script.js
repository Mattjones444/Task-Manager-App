document.addEventListener("DOMContentLoaded", function () {
    const modal = document.getElementById("taskModal");
    const openBtn = document.getElementById("openTaskModalBtn");
    const closeBtn = document.querySelector(".close-button");

    openBtn.addEventListener("click", function () {
        modal.style.display = "block";
    });

    closeBtn.addEventListener("click", function () {
        modal.style.display = "none";
    });

    window.addEventListener("click", function (event) {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    });

    // Optional: handle form submission via fetch or let the form handle it
    const taskForm = document.getElementById("taskForm");
    taskForm.addEventListener("submit", function (e) {
        e.preventDefault(); // prevent default form submission

        const formData = {
            title: document.getElementById("taskTitle").value,
            description: document.getElementById("taskDescription").value,
            setDate: document.getElementById("taskDueDate").value,
            status: "TO_DO" // default status
        };

        fetch("/api/tasks", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(formData)
        })
        .then(response => {
            if (response.ok) {
                alert("Task created successfully!");
                modal.style.display = "none";
                taskForm.reset();
            } else {
                return response.text().then(text => { throw new Error(text); });
            }
        })
        .catch(error => {
            console.error("Error creating task:", error);
            alert("Failed to create task.");
        });
    });
});
