import React from 'react'
import { Button, Form } from 'react-bootstrap';
import { useForm } from 'react-hook-form'
import { useNavigate } from 'react-router-dom';

function CartForm({ initialContents, submitAction, buttonLabel = "Create" }) {

    const navigate = useNavigate();
    
    // Stryker disable all
    const {
        register,
        formState: { errors },
        handleSubmit,
    } = useForm(
        { defaultValues: initialContents || {}, }
    );
    // Stryker enable all
   
    const testIdPrefix = "CartForm";

    return (

        <Form onSubmit={handleSubmit(submitAction)}>

            {initialContents && (
                <Form.Group className="mb-3" >
                    <Form.Label htmlFor="id">Id</Form.Label>
                    <Form.Control
                        data-testid={testIdPrefix + "-id"}
                        id="id"
                        type="text"
                        {...register("id")}
                        value={initialContents.id}
                        disabled
                    />
                </Form.Group>
            )}

            <Form.Group className="mb-3" >
                <Form.Label htmlFor="name">Name</Form.Label>
                <Form.Control
                    data-testid={testIdPrefix + "-name"}
                    id="name"
                    type="text"
                    isInvalid={Boolean(errors.name)}
                    {...register("name", {
                        required: "Name is required.",
                        maxLength : {
                            value: 30,
                            message: "Max length 30 characters"
                        }
                    })}
                />
                <Form.Control.Feedback type="invalid">
                    {errors.name?.message}
                </Form.Control.Feedback>
            </Form.Group>

            <Form.Group className="mb-3" >
                <Form.Label htmlFor="capacityPeople">CapacityPeople</Form.Label>
                <Form.Control
                    data-testid={testIdPrefix + "-capacityPeople"}
                    id="capacityPeople"
                    type="text"
                    isInvalid={Boolean(errors.capacityPeople)}
                    {...register("capacityPeople", {
                        required: "Capacity for People is required."
                    })}
                />
                <Form.Control.Feedback type="invalid">
                    {errors.capacityPeople?.message}
                </Form.Control.Feedback>
            </Form.Group>

            <Form.Group className="mb-3" >
                <Form.Label htmlFor="capacityWheelchair">CapacityWheelchair</Form.Label>
                <Form.Control
                    data-testid={testIdPrefix + "-capacityWheelchair"}
                    id="capacityWheelchair"
                    type="text"
                    isInvalid={Boolean(errors.capacityWheelchair)}
                    {...register("capacityWheelchair", {
                        required: "Capacity for wheelchair is required."
                    })}
                />
                <Form.Control.Feedback type="invalid">
                    {errors.capacityWheelchair?.message}
                </Form.Control.Feedback>
            </Form.Group>

        
            <Button
                type="submit"
                data-testid={testIdPrefix + "-submit"}
            >
                {buttonLabel}
            </Button>
            <Button
                variant="Secondary"
                onClick={() => navigate(-1)}
                data-testid={testIdPrefix + "-cancel"}
            >
                Cancel
            </Button>

        </Form>

    )
}

export default CartForm;